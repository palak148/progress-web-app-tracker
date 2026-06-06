const API_BASE = import.meta.env.VITE_API_URL || ''

export interface User {
  id: number
  email: string
  name: string
  provider: 'LOCAL' | 'GOOGLE'
}

export interface AuthResponse {
  token: string
  user: User
}

export interface Task {
  id: number
  prepPlanId?: number
  title: string
  subject?: string
  plannedDate: string
  estimatedMinutes?: number
  status: 'PENDING' | 'COMPLETED' | 'SKIPPED'
  completedAt?: string
  createdAt: string
}

export interface PrepPlan {
  id: number
  name: string
  examType?: string
  startDate?: string
  endDate?: string
  createdAt: string
}

export interface StreakStats {
  currentStreak: number
  longestStreak: number
  activeDaysThisMonth: number
  completedTasksThisMonth: number
  plannedTasksThisMonth: number
}

export interface HeatmapData {
  year: number
  days: Record<string, number>
}

export interface Note {
  id: number
  projectId: number
  title: string
  content: string
  subject?: string
  createdAt: string
  updatedAt: string
}

export interface Flashcard {
  id: number
  projectId: number
  front: string
  back: string
  subject?: string
  mastered: boolean
  createdAt: string
}

function getToken(): string | null {
  return localStorage.getItem('token')
}

function projectQuery(projectId?: number): string {
  return projectId != null ? `&projectId=${projectId}` : ''
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> || {})
  }

  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  } catch {
    throw new Error('Cannot reach backend — is it running?')
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: response.status === 401
        ? 'Session expired — please log in again'
        : `Request failed (${response.status})`
    }))
    throw new Error(error.message || `Request failed (${response.status})`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  try {
    return await response.json()
  } catch {
    throw new Error('Invalid response from server')
  }
}

export const api = {
  register: (data: { name: string; email: string; password: string }) =>
    request<AuthResponse>('/api/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  login: (data: { email: string; password: string }) =>
    request<AuthResponse>('/api/auth/login', { method: 'POST', body: JSON.stringify(data) }),

  me: () => request<User>('/api/auth/me'),

  getProjects: () => request<PrepPlan[]>('/api/plans'),

  getProject: (id: number) => request<PrepPlan>(`/api/plans/${id}`),

  createProject: (data: { name: string; examType?: string; startDate?: string; endDate?: string }) =>
    request<PrepPlan>('/api/plans', { method: 'POST', body: JSON.stringify(data) }),

  deleteProject: (id: number) => request<void>(`/api/plans/${id}`, { method: 'DELETE' }),

  getTasks: (date: string, projectId?: number) =>
    request<Task[]>(`/api/tasks?date=${date}${projectQuery(projectId)}`),

  createTask: (data: {
    title: string
    subject?: string
    plannedDate: string
    estimatedMinutes?: number
    prepPlanId?: number
  }) => request<Task>('/api/tasks', { method: 'POST', body: JSON.stringify(data) }),

  completeTask: (id: number) => request<Task>(`/api/tasks/${id}/complete`, { method: 'PATCH' }),

  uncompleteTask: (id: number) => request<Task>(`/api/tasks/${id}/uncomplete`, { method: 'PATCH' }),

  deleteTask: (id: number) => request<void>(`/api/tasks/${id}`, { method: 'DELETE' }),

  getStreak: (projectId?: number) =>
    request<StreakStats>(`/api/stats/streak?${projectId != null ? `projectId=${projectId}` : ''}`),

  getHeatmap: (year: number, projectId?: number) =>
    request<HeatmapData>(`/api/stats/heatmap?year=${year}${projectQuery(projectId)}`),

  getNotes: (projectId: number) => request<Note[]>(`/api/notes?projectId=${projectId}`),

  createNote: (data: { projectId: number; title: string; content: string; subject?: string }) =>
    request<Note>('/api/notes', { method: 'POST', body: JSON.stringify(data) }),

  updateNote: (id: number, data: { projectId: number; title: string; content: string; subject?: string }) =>
    request<Note>(`/api/notes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  deleteNote: (id: number) => request<void>(`/api/notes/${id}`, { method: 'DELETE' }),

  getFlashcards: (projectId: number) => request<Flashcard[]>(`/api/flashcards?projectId=${projectId}`),

  createFlashcard: (data: { projectId: number; front: string; back: string; subject?: string }) =>
    request<Flashcard>('/api/flashcards', { method: 'POST', body: JSON.stringify(data) }),

  updateFlashcard: (id: number, data: { projectId: number; front: string; back: string; subject?: string }) =>
    request<Flashcard>(`/api/flashcards/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  toggleFlashcardMastered: (id: number) =>
    request<Flashcard>(`/api/flashcards/${id}/mastered`, { method: 'PATCH' }),

  deleteFlashcard: (id: number) => request<void>(`/api/flashcards/${id}`, { method: 'DELETE' })
}

export function getGoogleLoginUrl(): string {
  return `${API_BASE}/oauth2/authorization/google`
}
