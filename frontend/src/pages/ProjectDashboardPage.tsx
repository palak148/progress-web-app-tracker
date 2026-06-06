import { useCallback, useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { api, PrepPlan } from '../api/client'
import { AppHeader } from '../components/AppHeader'
import { TrackerTab } from '../components/TrackerTab'
import { NotesPanel } from '../components/NotesPanel'
import { FlashcardsPanel } from '../components/FlashcardsPanel'

type Tab = 'tracker' | 'notes' | 'flashcards'

const tabs: { id: Tab; label: string }[] = [
  { id: 'tracker', label: 'Tracker' },
  { id: 'notes', label: 'Notes' },
  { id: 'flashcards', label: 'Flashcards' }
]

export default function ProjectDashboardPage() {
  const { projectId } = useParams<{ projectId: string }>()
  const planId = Number(projectId)
  const { user } = useAuth()
  const [project, setProject] = useState<PrepPlan | null>(null)
  const [activeTab, setActiveTab] = useState<Tab>('tracker')
  const [error, setError] = useState('')

  const setErrorMsg = useCallback((msg: string) => setError(msg), [])

  useEffect(() => {
    if (!planId) return
    api.getProject(planId).then(setProject).catch((err) => {
      setError(err instanceof Error ? err.message : 'Failed to load project')
    })
  }, [planId])

  return (
    <div className="min-h-screen">
      <AppHeader
        title={project?.name || 'Project'}
        subtitle={`Hi, ${user?.name}`}
        backTo="/"
        backLabel="All projects"
      />

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">
        {project?.examType && (
          <p className="text-sm text-slate-400 -mt-2">{project.examType}</p>
        )}

        <nav className="flex gap-1 p-1 bg-slate-900 border border-slate-800 rounded-xl w-fit">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => { setActiveTab(tab.id); setError('') }}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                activeTab === tab.id
                  ? 'bg-brand-600 text-white'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>

        {error && (
          <div className="bg-red-900/30 border border-red-800 text-red-300 px-4 py-3 rounded-lg text-sm">
            {error}
          </div>
        )}

        {activeTab === 'tracker' && <TrackerTab projectId={planId} onError={setErrorMsg} />}
        {activeTab === 'notes' && <NotesPanel projectId={planId} onError={setErrorMsg} />}
        {activeTab === 'flashcards' && <FlashcardsPanel projectId={planId} onError={setErrorMsg} />}
      </main>
    </div>
  )
}
