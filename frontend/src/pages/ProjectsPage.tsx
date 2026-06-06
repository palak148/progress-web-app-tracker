import { FormEvent, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { api, PrepPlan } from '../api/client'
import { AppHeader } from '../components/AppHeader'

export default function ProjectsPage() {
  const { user } = useAuth()
  const [projects, setProjects] = useState<PrepPlan[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [name, setName] = useState('')
  const [examType, setExamType] = useState('')
  const [creating, setCreating] = useState(false)

  const loadProjects = async () => {
    setLoading(true)
    setError('')
    try {
      setProjects(await api.getProjects())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load projects')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProjects()
  }, [])

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    setCreating(true)
    setError('')
    try {
      await api.createProject({
        name: name.trim(),
        examType: examType.trim() || undefined
      })
      setName('')
      setExamType('')
      setShowForm(false)
      await loadProjects()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create project')
    } finally {
      setCreating(false)
    }
  }

  const handleDelete = async (id: number, projectName: string) => {
    if (!window.confirm(`Delete "${projectName}" and all its tasks?`)) return
    try {
      await api.deleteProject(id)
      await loadProjects()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete project')
    }
  }

  return (
    <div className="min-h-screen">
      <AppHeader title="Prep Tracker" subtitle={`Hi, ${user?.name}`} />

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-semibold">Your projects</h2>
            <p className="text-sm text-slate-400">Each project has its own tasks, streak & heatmap</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 font-medium text-sm transition"
          >
            {showForm ? 'Cancel' : '+ New project'}
          </button>
        </div>

        {error && (
          <div className="bg-red-900/30 border border-red-800 text-red-300 px-4 py-3 rounded-lg text-sm">
            {error}
          </div>
        )}

        {showForm && (
          <form onSubmit={handleCreate} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3">
            <input
              type="text"
              placeholder="Project name (e.g. RBI Grade B 2026)"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-4 py-3 rounded-lg bg-slate-800 border border-slate-700"
              required
            />
            <input
              type="text"
              placeholder="Type / exam (optional)"
              value={examType}
              onChange={(e) => setExamType(e.target.value)}
              className="w-full px-4 py-3 rounded-lg bg-slate-800 border border-slate-700"
            />
            <button
              type="submit"
              disabled={creating}
              className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 font-medium transition disabled:opacity-50"
            >
              {creating ? 'Creating...' : 'Create project'}
            </button>
          </form>
        )}

        {loading ? (
          <p className="text-slate-500">Loading projects...</p>
        ) : projects.length === 0 ? (
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-8 text-center">
            <p className="text-slate-400 mb-2">No projects yet</p>
            <p className="text-sm text-slate-500">Create one to start tracking — RBI Grade B, UPSC, or anything else.</p>
          </div>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {projects.map((project) => (
              <div
                key={project.id}
                className="group bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-brand-700 transition"
              >
                <Link to={`/projects/${project.id}`} className="block">
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-xl bg-brand-900/50 border border-brand-700 flex items-center justify-center text-lg flex-shrink-0">
                      📁
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold truncate group-hover:text-brand-400 transition">
                        {project.name}
                      </h3>
                      {project.examType && (
                        <p className="text-sm text-slate-400 truncate">{project.examType}</p>
                      )}
                      <p className="text-xs text-slate-500 mt-1">Open tracker →</p>
                    </div>
                  </div>
                </Link>
                <button
                  onClick={() => handleDelete(project.id, project.name)}
                  className="mt-3 text-xs text-slate-500 hover:text-red-400 transition"
                >
                  Delete project
                </button>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
