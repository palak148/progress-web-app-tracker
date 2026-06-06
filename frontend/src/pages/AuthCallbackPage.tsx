import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function AuthCallbackPage() {
  const [params] = useSearchParams()
  const { setToken } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    const token = params.get('token')
    if (token) {
      setToken(token)
        .then(() => navigate('/', { replace: true }))
        .catch(() => navigate('/login', { replace: true }))
    } else {
      navigate('/login', { replace: true })
    }
  }, [params, setToken, navigate])

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-slate-400">Completing sign in...</div>
    </div>
  )
}
