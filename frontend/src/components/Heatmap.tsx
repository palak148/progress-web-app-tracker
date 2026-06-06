import { HeatmapData } from '../api/client'

interface Props {
  data: HeatmapData
}

function getColor(count: number): string {
  if (count === 0) return 'bg-slate-800'
  if (count === 1) return 'bg-emerald-900'
  if (count === 2) return 'bg-emerald-700'
  if (count <= 4) return 'bg-emerald-500'
  return 'bg-emerald-400'
}

export function Heatmap({ data }: Props) {
  const start = new Date(data.year, 0, 1)
  const end = new Date(data.year, 11, 31)
  const cells: { date: string; count: number }[] = []

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const key = d.toISOString().slice(0, 10)
    cells.push({ date: key, count: data.days[key] || 0 })
  }

  return (
    <div className="overflow-x-auto">
      <div className="grid grid-flow-col grid-rows-7 gap-1 min-w-max">
        {cells.map((cell) => (
          <div
            key={cell.date}
            title={`${cell.date}: ${cell.count} completed`}
            className={`w-3 h-3 rounded-sm ${getColor(cell.count)}`}
          />
        ))}
      </div>
      <p className="text-xs text-slate-500 mt-2">{data.year} activity</p>
    </div>
  )
}
