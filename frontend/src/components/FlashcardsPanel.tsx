import { FormEvent, useCallback, useEffect, useState } from 'react'
import { api, Flashcard } from '../api/client'

interface Props {
  projectId: number
  onError: (msg: string) => void
}

export function FlashcardsPanel({ projectId, onError }: Props) {
  const [cards, setCards] = useState<Flashcard[]>([])
  const [loading, setLoading] = useState(true)
  const [front, setFront] = useState('')
  const [back, setBack] = useState('')
  const [subject, setSubject] = useState('')
  const [studyMode, setStudyMode] = useState(false)
  const [studyIndex, setStudyIndex] = useState(0)
  const [flipped, setFlipped] = useState(false)
  const [filterSubject, setFilterSubject] = useState('')

  const loadCards = useCallback(async () => {
    setLoading(true)
    try {
      setCards(await api.getFlashcards(projectId))
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to load flashcards')
    } finally {
      setLoading(false)
    }
  }, [projectId, onError])

  useEffect(() => {
    loadCards()
  }, [loadCards])

  const subjects = Array.from(new Set(cards.map((c) => c.subject).filter(Boolean))) as string[]

  const filtered = filterSubject
    ? cards.filter((c) => c.subject === filterSubject)
    : cards

  const studyCards = filtered.filter((c) => !c.mastered)

  const handleAdd = async (e: FormEvent) => {
    e.preventDefault()
    if (!front.trim() || !back.trim()) return
    try {
      await api.createFlashcard({
        projectId,
        front: front.trim(),
        back: back.trim(),
        subject: subject.trim() || undefined
      })
      setFront('')
      setBack('')
      await loadCards()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to add flashcard')
    }
  }

  const handleToggleMastered = async (id: number) => {
    try {
      await api.toggleFlashcardMastered(id)
      await loadCards()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to update flashcard')
    }
  }

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this flashcard?')) return
    try {
      await api.deleteFlashcard(id)
      await loadCards()
    } catch (err) {
      onError(err instanceof Error ? err.message : 'Failed to delete flashcard')
    }
  }

  const currentStudy = studyCards[studyIndex]

  return (
    <div className="space-y-4">
      {studyMode && studyCards.length > 0 ? (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center">
          <p className="text-xs text-slate-500 mb-4">
            Study mode · {studyIndex + 1} / {studyCards.length}
            {currentStudy?.subject && ` · ${currentStudy.subject}`}
          </p>
          <button
            onClick={() => setFlipped(!flipped)}
            className="w-full max-w-lg mx-auto min-h-[160px] p-6 rounded-2xl bg-slate-800 border border-slate-700 hover:border-brand-600 transition flex items-center justify-center"
          >
            <p className="text-lg whitespace-pre-wrap">
              {flipped ? currentStudy?.back : currentStudy?.front}
            </p>
          </button>
          <p className="text-xs text-slate-500 mt-2">Tap card to flip</p>
          <div className="flex justify-center gap-2 mt-4 flex-wrap">
            <button
              onClick={() => { setFlipped(false); setStudyIndex(Math.max(0, studyIndex - 1)) }}
              disabled={studyIndex === 0}
              className="px-3 py-2 rounded-lg bg-slate-800 text-sm disabled:opacity-40"
            >
              Previous
            </button>
            <button
              onClick={async () => {
                if (currentStudy) await handleToggleMastered(currentStudy.id)
                setFlipped(false)
                if (studyIndex >= studyCards.length - 1) setStudyIndex(0)
              }}
              className="px-3 py-2 rounded-lg bg-emerald-800 text-emerald-100 text-sm"
            >
              Mark mastered
            </button>
            <button
              onClick={() => { setFlipped(false); setStudyIndex(Math.min(studyCards.length - 1, studyIndex + 1)) }}
              disabled={studyIndex >= studyCards.length - 1}
              className="px-3 py-2 rounded-lg bg-slate-800 text-sm disabled:opacity-40"
            >
              Next
            </button>
            <button
              onClick={() => setStudyMode(false)}
              className="px-3 py-2 rounded-lg border border-slate-700 text-sm"
            >
              Exit study
            </button>
          </div>
        </div>
      ) : (
        <>
          <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
            <h2 className="font-semibold mb-3">Add flashcard</h2>
            <div className="grid gap-2 sm:grid-cols-3 mb-2">
              <input
                type="text"
                placeholder="Subject / column"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                className="px-3 py-2 rounded-lg bg-slate-800 border border-slate-700 text-sm"
              />
              <input
                type="text"
                placeholder="Front (question)"
                value={front}
                onChange={(e) => setFront(e.target.value)}
                className="px-3 py-2 rounded-lg bg-slate-800 border border-slate-700 text-sm"
                required
              />
              <input
                type="text"
                placeholder="Back (answer)"
                value={back}
                onChange={(e) => setBack(e.target.value)}
                className="px-3 py-2 rounded-lg bg-slate-800 border border-slate-700 text-sm"
                required
              />
            </div>
            <div className="flex gap-2">
              <button type="submit" className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-700 text-sm font-medium">
                Add card
              </button>
              {studyCards.length > 0 && (
                <button
                  type="button"
                  onClick={() => { setStudyMode(true); setStudyIndex(0); setFlipped(false) }}
                  className="px-4 py-2 rounded-lg border border-brand-700 text-brand-400 text-sm"
                >
                  Study ({studyCards.length})
                </button>
              )}
            </div>
          </form>

          {subjects.length > 0 && (
            <div className="flex gap-2 flex-wrap">
              <button
                onClick={() => setFilterSubject('')}
                className={`px-3 py-1 rounded-full text-xs border ${
                  !filterSubject ? 'bg-brand-900/40 border-brand-700' : 'border-slate-700 text-slate-400'
                }`}
              >
                All
              </button>
              {subjects.map((s) => (
                <button
                  key={s}
                  onClick={() => setFilterSubject(s)}
                  className={`px-3 py-1 rounded-full text-xs border ${
                    filterSubject === s ? 'bg-brand-900/40 border-brand-700' : 'border-slate-700 text-slate-400'
                  }`}
                >
                  {s}
                </button>
              ))}
            </div>
          )}

          <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 text-left">
                  <th className="px-4 py-3 font-medium w-32">Subject</th>
                  <th className="px-4 py-3 font-medium">Front</th>
                  <th className="px-4 py-3 font-medium">Back</th>
                  <th className="px-4 py-3 font-medium w-24">Status</th>
                  <th className="px-4 py-3 font-medium w-28"></th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={5} className="px-4 py-6 text-slate-500">Loading...</td></tr>
                ) : filtered.length === 0 ? (
                  <tr><td colSpan={5} className="px-4 py-6 text-slate-500">No flashcards yet. Add one above.</td></tr>
                ) : (
                  filtered.map((card) => (
                    <tr key={card.id} className="border-b border-slate-800/60 hover:bg-slate-800/30">
                      <td className="px-4 py-3 text-slate-400">{card.subject || '—'}</td>
                      <td className="px-4 py-3">{card.front}</td>
                      <td className="px-4 py-3 text-slate-300">{card.back}</td>
                      <td className="px-4 py-3">
                        <button
                          onClick={() => handleToggleMastered(card.id)}
                          className={`text-xs px-2 py-1 rounded-full ${
                            card.mastered
                              ? 'bg-emerald-900/50 text-emerald-400'
                              : 'bg-slate-800 text-slate-400'
                          }`}
                        >
                          {card.mastered ? 'Mastered' : 'Learning'}
                        </button>
                      </td>
                      <td className="px-4 py-3">
                        <button
                          onClick={() => handleDelete(card.id)}
                          className="text-xs text-slate-500 hover:text-red-400"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}
