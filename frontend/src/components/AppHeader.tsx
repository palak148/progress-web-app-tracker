import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

interface AppHeaderProps {
  title: string
  subtitle?: string
  backTo?: string
  backLabel?: string
}

export function AppHeader({ title, subtitle, backTo, backLabel }: AppHeaderProps) {
  const { logout } = useAuth()

  return (
    <header className="border-b border-slate-800 bg-slate-900/80 backdrop-blur sticky top-0 z-10">
      <div className="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
        <div>
          {backTo && (
            <Link to={backTo} className="text-xs text-brand-500 hover:underline mb-1 inline-block">
              ← {backLabel || 'Back'}
            </Link>
          )}
          <h1 className="text-xl font-bold">{title}</h1>
          {subtitle && <p className="text-sm text-slate-400">{subtitle}</p>}
        </div>
        <button
          onClick={logout}
          className="text-sm text-slate-400 hover:text-white transition"
        >
          Logout
        </button>
      </div>
    </header>
  )
}
