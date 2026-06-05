// frontend/src/components/ErrorCard.tsx

import type { ErrorResponse } from '../types';

interface ErrorCardProps {
  error: ErrorResponse;
  onRetry: () => void;
}

export default function ErrorCard({ error, onRetry }: ErrorCardProps) {
  return (
    <div className="max-w-lg mx-auto animate-fade-in w-full">
      <div className="clean-card p-8 flex flex-col items-center gap-6">
        <div className="w-16 h-16 rounded-2xl bg-red-50 border border-red-100 flex items-center justify-center">
          <svg
            className="w-8 h-8 text-red-600"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"
            />
          </svg>
        </div>

        <div className="text-center space-y-3">
          <div className="inline-flex items-center gap-2">
            <span className="px-2.5 py-1 rounded-md bg-red-100 text-red-800 text-xs font-mono font-bold">
              {error.status}
            </span>
            <span className="text-sm font-bold text-red-600">
              {error.error}
            </span>
          </div>
          <p className="text-surface-600 text-sm leading-relaxed max-w-md">
            {error.message}
          </p>
        </div>

        <button
          id="try-again-button"
          onClick={onRetry}
          className="btn-primary mt-2"
        >
          <span className="flex items-center gap-2">
            <svg
              className="w-4 h-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182"
              />
            </svg>
            Try Again
          </span>
        </button>
      </div>
    </div>
  );
}
