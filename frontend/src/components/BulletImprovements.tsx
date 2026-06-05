// frontend/src/components/BulletImprovements.tsx

import type { BulletImprovement } from '../types';

interface BulletImprovementsProps {
  improvements: BulletImprovement[];
}

export default function BulletImprovements({
  improvements,
}: BulletImprovementsProps) {
  return (
    <div className="animate-fade-in">
      <div className="flex items-center gap-3 mb-6">
        <div className="w-8 h-8 rounded-lg bg-white border border-surface-200 flex items-center justify-center shadow-sm">
          <svg
            className="w-4 h-4 text-surface-900"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"
            />
          </svg>
        </div>
        <div>
          <h3 className="text-lg font-bold text-surface-900 tracking-tight">
            Bullet Point Improvements
          </h3>
          <p className="text-sm text-surface-500">
            {improvements.length} suggestions to strengthen your resume
          </p>
        </div>
      </div>

      <div className="space-y-4">
        {improvements.map((item, i) => (
          <div key={i} className="clean-card p-6 space-y-4">
            <div className="flex items-center gap-2 mb-2">
              <span className="w-6 h-6 rounded-md bg-surface-100 text-surface-700 border border-surface-200 text-xs font-bold flex items-center justify-center">
                {i + 1}
              </span>
              <span className="text-xs font-bold text-surface-400 uppercase tracking-widest">
                Improvement
              </span>
            </div>

            <div>
              <span className="text-xs font-bold text-red-500 uppercase tracking-widest">
                Original
              </span>
              <p className="text-sm text-surface-500 line-through mt-1.5 leading-relaxed">
                {item.original}
              </p>
            </div>

            <div>
              <span className="text-xs font-bold text-emerald-600 uppercase tracking-widest">
                Suggested
              </span>
              <p className="text-sm font-medium text-surface-900 mt-1.5 leading-relaxed bg-emerald-50 border-l-4 border-emerald-500 pl-4 py-3 rounded-r-md">
                {item.suggested}
              </p>
            </div>

            <div className="pt-2">
              <span className="text-xs font-bold text-primary-600 uppercase tracking-widest">
                Why
              </span>
              <p className="text-sm text-surface-600 italic mt-1.5 leading-relaxed">
                {item.reason}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
