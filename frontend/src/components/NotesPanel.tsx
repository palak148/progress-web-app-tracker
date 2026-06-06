import { FormEvent, useCallback, useEffect, useRef, useState } from 'react'
import { api, Note } from '../api/client'

interface Props {
  projectId: number
  onError: (msg: string) => void
}

const emptyNote = { title: '', content: '', subject: '' }

export function NotesPanel({ projectId, onError }: Props) {
  const [notes, setNotes] = useState<Note[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [draft, setDraft] = useState(emptyNote)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const loadNotes = useCallback(async () => {
    setLoading(true)
    try {
      const list = await api.getNotes(projectId)
      setNotes(list)
      setSelectedId((prev) => {
        if (prev && list.some((n) => n.id === prev)) return prev
        return list.length > 0 ? list[0].id : null
      })
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to load notes')
    } finally {
      setLoading(false)
    }
  }, [projectId, onError])

  useEffect(() => {
    loadNotes()
  }, [loadNotes])

  useEffect(() => {
    const note = notes.find((n) => n.id === selectedId)
    if (note) {
      setDraft({ title: note.title, content: note.content, subject: note.subject || '' })
    }
  }, [selectedId, notes])

  const selectNote = (note: Note) => {
    setSelectedId(note.id)
    setDraft({ title: note.title, content: note.content, subject: note.subject || '' })
  }

  const handleNew = () => {
    setSelectedId(null)
    setDraft(emptyNote)
  }

  const handleSave = async (e?: FormEvent) => {
    e?.preventDefault()
    if (!draft.title.trim()) return
    setSaving(true)
    try {
      const payload = {
        projectId,
        title: draft.title.trim(),
        content: draft.content,
        subject: draft.subject.trim() || undefined
      }
      if (selectedId) {
        await api.updateNote(selectedId, payload)
      } else {
        const created = await api.createNote(payload)
        setSelectedId(created.id)
      }
      await loadNotes()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to save note')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!selectedId) return
    if (!window.confirm('Delete this note?')) return
    try {
      await api.deleteNote(selectedId)
      setSelectedId(null)
      setDraft(emptyNote)
      await loadNotes()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to delete note')
    }
  }

  const handleImportFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = () => {
      const text = String(reader.result || '')
      setSelectedId(null)
      setDraft({
        title: file.name.replace(/\.[^.]+$/, '') || 'Imported note',
        content: text,
        subject: 'Syllabus'
      })
    }
    reader.readAsText(file)
    e.target.value = ''
  }

  const handleImportSyllabus = () => {
    setSelectedId(null)
    setDraft({
      title: 'Syllabus',
      content: '',
      subject: 'Syllabus'
    })
  }

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
      <div className="flex flex-col lg:flex-row min-h-[480px]">
        <aside className="lg:w-64 border-b lg:border-b-0 lg:border-r border-slate-800 p-4">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-sm">Notes</h2>
            <button
              onClick={handleNew}
              className="text-xs text-brand-500 hover:underline"
            >
              + New
            </button>
          </div>

          <div className="flex gap-2 mb-3">
            <button
              onClick={handleImportSyllabus}
              className="flex-1 text-xs px-2 py-1.5 rounded-lg bg-slate-800 border border-slate-700 hover:border-brand-600 transition"
            >
              Syllabus
            </button>
            <button
              onClick={() => fileInputRef.current?.click()}
              className="flex-1 text-xs px-2 py-1.5 rounded-lg bg-slate-800 border border-slate-700 hover:border-brand-600 transition"
            >
              Import .txt
            </button>
            <input ref={fileInputRef} type="file" accept=".txt,.md" className="hidden" onChange={handleImportFile} />
          </div>

          {loading ? (
            <p className="text-slate-500 text-sm">Loading...</p>
          ) : notes.length === 0 ? (
            <p className="text-slate-500 text-sm">No notes yet. Create one or import syllabus.</p>
          ) : (
            <ul className="space-y-1 max-h-80 overflow-y-auto">
              {notes.map((note) => (
                <li key={note.id}>
                  <button
                    onClick={() => selectNote(note)}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm transition ${
                      selectedId === note.id
                        ? 'bg-brand-900/40 border border-brand-700 text-white'
                        : 'hover:bg-slate-800 text-slate-300'
                    }`}
                  >
                    <p className="font-medium truncate">{note.title}</p>
                    {note.subject && (
                      <p className="text-xs text-slate-500 truncate">{note.subject}</p>
                    )}
                  </button>
                </li>
              ))}
            </ul>
          )}
        </aside>

        <div className="flex-1 p-4 flex flex-col">
          <form onSubmit={handleSave} className="flex flex-col flex-1 gap-3">
            <div className="flex flex-col sm:flex-row gap-2">
              <input
                type="text"
                placeholder="Note title"
                value={draft.title}
                onChange={(e) => setDraft({ ...draft, title: e.target.value })}
                className="flex-1 px-3 py-2 rounded-lg bg-slate-800 border border-slate-700"
                required
              />
              <input
                type="text"
                placeholder="Subject / tag"
                value={draft.subject}
                onChange={(e) => setDraft({ ...draft, subject: e.target.value })}
                className="sm:w-40 px-3 py-2 rounded-lg bg-slate-800 border border-slate-700"
              />
            </div>
            <textarea
              placeholder="Write notes, paste syllabus, import content..."
              value={draft.content}
              onChange={(e) => setDraft({ ...draft, content: e.target.value })}
              className="flex-1 min-h-[280px] px-3 py-2 rounded-lg bg-slate-800 border border-slate-700 resize-y font-mono text-sm leading-relaxed"
            />
            <div className="flex gap-2">
              <button
                type="submit"
                disabled={saving}
                className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 font-medium text-sm transition disabled:opacity-50"
              >
                {saving ? 'Saving...' : selectedId ? 'Save changes' : 'Create note'}
              </button>
              {selectedId && (
                <button
                  type="button"
                  onClick={handleDelete}
                  className="px-4 py-2 rounded-lg border border-red-800 text-red-400 hover:bg-red-900/20 text-sm transition"
                >
                  Delete
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
