import { createContext, useContext, useEffect, useState, ReactNode } from 'react'
import { api, User } from '../api/client'

interface AuthContextType {
  user: User | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (name: string, email: string, password: string) => Promise<void>
  logout: () => void
  setToken: (token: string) => Promise<void>
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      api.me()
        .then(setUser)
        .catch(() => localStorage.removeItem('token'))
        .finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [])

  const setToken = async (token: string) => {
    localStorage.setItem('token', token)
    const me = await api.me()
    setUser(me)
  }

  const login = async (email: string, password: string) => {
    const res = await api.login({ email, password })
    localStorage.setItem('token', res.token)
    setUser(res.user)
  }

  const register = async (name: string, email: string, password: string) => {
    const res = await api.register({ name, email, password })
    localStorage.setItem('token', res.token)
    setUser(res.user)
  }

  const logout = () => {
    localStorage.removeItem('token')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, setToken }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
