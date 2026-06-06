import { FormEvent, useCallback, useEffect, useState } from 'react'
import { api, StreakStats, Task, HeatmapData } from '../api/client'
import { Heatmap } from './Heatmap'

interface Props {
  projectId: number
  onError: (msg: string) => void
}

function todayStr(): string {
  return new Date().toISOString().slice(0, 10)
}

export function TrackerTab({ projectId, onError }: Props) {
  const [selectedDate, setSelectedDate] = useState(todayStr())
  const [tasks, setTasks] = useState<Task[]>([])
  const [stats, setStats] = useState<StreakStats | null>(null)
  const [heatmap, setHeatmap] = useState<HeatmapData | null>(null)
  const [title, setTitle] = useState('')
  const [subject, setSubject] = useState('')
  const [loading, setLoading] = useState(true)

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const [taskList, streak, map] = await Promise.all([
        api.getTasks(selectedDate, projectId),
        api.getStreak(projectId),
        api.getHeatmap(new Date().getFullYear(), projectId)
      ])
      setTasks(taskList)
      setStats(streak)
      setHeatmap(map)
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to load tracker')
    } finally {
      setLoading(false)
    }
  }, [selectedDate, projectId, onError])

  useEffect(() => {
    loadData()
  }, [loadData])

  const handleAddTask = async (e: FormEvent) => {
    e.preventDefault()
    if (!title.trim()) return
    try {
      await api.createTask({
        title: title.trim(),
        subject: subject.trim() || undefined,
        plannedDate: selectedDate,
        prepPlanId: projectId
      })
      setTitle('')
      setSubject('')
      await loadData()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to add task')
    }
  }

  const toggleTask = async (task: Task) => {
    try {
      if (task.status === 'COMPLETED') await api.uncompleteTask(task.id)
      else await api.completeTask(task.id)
      await loadData()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to update task')
    }
  }

  const removeTask = async (id: number) => {
    try {
      await api.deleteTask(id)
      await loadData()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to delete task')
    }
  }

  const completedToday = tasks.filter((t) => t.status === 'COMPLETED').length

  return (
    <div className="space-y-6">
      <section className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <StatCard label="Current streak" value={stats?.currentStreak ?? 0} suffix="days" highlight />
        <StatCard label="Longest streak" value={stats?.longestStreak ?? 0} suffix="days" />
        <StatCard label="Active this month" value={stats?.activeDaysThisMonth ?? 0} suffix="days" />
        <StatCard
          label="Tasks this month"
          value={stats?.completedTasksThisMonth ?? 0}
          suffix={`/ ${stats?.plannedTasksThisMonth ?? 0}`}
        />
      </section>

      {heatmap && (
        <section className="bg-slate-900 border border-slate-800 rounded-2xl p-5">
          <h2 className="font-semibold mb-4">Activity heatmap</h2>
          <Heatmap data={heatmap} />
        </section>
      )}

      <section className="bg-slate-900 border border-slate-800 rounded-2xl p-5">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
          <h2 className="font-semibold">Daily tasks</h2>
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="px-3 py-2 rounded-lg bg-slate-800 border border-slate-700 text-sm"
          />
        </div>

        <form onSubmit={handleAddTask} className="flex flex-col sm:flex-row gap-2 mb-4">
          <input
            type="text"
            placeholder="Task title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="flex-1 px-3 py-2 rounded-lg bg-slate-800 border border-slate-700"
          />
          <input
            type="text"
            placeholder="Subject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            className="sm:w-40 px-3 py-2 rounded-lg bg-slate-800 border border-slate-700"
          />
          <button type="submit" className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 font-medium transition">
            Add
          </button>
        </form>

        <p className="text-sm text-slate-400 mb-3">
          {completedToday}/{tasks.length} completed for {selectedDate}
        </p>

        {loading ? (
          <p className="text-slate-500">Loading tasks...</p>
        ) : tasks.length === 0 ? (
          <p className="text-slate-500">No tasks for this day.</p>
        ) : (
          <ul className="space-y-2">
            {tasks.map((task) => (
              <li key={task.id} className="flex items-center gap-3 p-3 rounded-lg bg-slate-800/60 border border-slate-700/50">
                <button
                  onClick={() => toggleTask(task)}
                  className={`w-5 h-5 rounded border flex-shrink-0 flex items-center justify-center ${
                    task.status === 'COMPLETED' ? 'bg-emerald-600 border-emerald-600' : 'border-slate-500 hover:border-brand-500'
                  }`}
                >
                  {task.status === 'COMPLETED' && <span className="text-white text-xs">✓</span>}
                </button>
                <div className="flex-1 min-w-0">
                  <p className={`truncate ${task.status === 'COMPLETED' ? 'line-through text-slate-500' : ''}`}>{task.title}</p>
                  {task.subject && <p className="text-xs text-slate-500">{task.subject}</p>}
                </div>
                <button onClick={() => removeTask(task.id)} className="text-slate-500 hover:text-red-400 text-sm">Delete</button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

function StatCard({ label, value, suffix, highlight }: { label: string; value: number; suffix: string; highlight?: boolean }) {
  return (
    <div className={`rounded-2xl p-4 border ${highlight ? 'bg-brand-900/30 border-brand-700' : 'bg-slate-900 border-slate-800'}`}>
      <p className="text-xs text-slate-400 mb-1">{label}</p>
      <p className="text-2xl font-bold">
        {value}<span className="text-sm font-normal text-slate-400 ml-1">{suffix}</span>
      </p>
    </div>
  )
}
