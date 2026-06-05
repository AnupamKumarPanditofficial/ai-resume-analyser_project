// frontend/src/components/KeywordGrid.tsx

interface KeywordGridProps {
  matchedKeywords: string[];
  missingKeywords: string[];
}

export default function KeywordGrid({
  matchedKeywords,
  missingKeywords,
}: KeywordGridProps) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-fade-in">
      <div className="clean-card p-6">
        <div className="flex items-center gap-3 mb-5">
          <div className="w-8 h-8 rounded-lg bg-emerald-50 border border-emerald-100 flex items-center justify-center">
            <svg
              className="w-4 h-4 text-emerald-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2.5}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M4.5 12.75l6 6 9-13.5"
              />
            </svg>
          </div>
          <div>
            <h3 className="text-base font-bold text-surface-900 tracking-tight">
              Matched Keywords
            </h3>
            <p className="text-sm text-surface-500">
              {matchedKeywords.length} found in your resume
            </p>
          </div>
        </div>
        <div className="flex flex-wrap gap-2">
          {matchedKeywords.length === 0 ? (
            <span className="text-sm text-surface-400 italic">No matches found.</span>
          ) : (
            matchedKeywords.map((keyword, i) => (
              <span key={i} className="pill-green">
                {keyword}
              </span>
            ))
          )}
        </div>
      </div>

      <div className="clean-card p-6">
        <div className="flex items-center gap-3 mb-5">
          <div className="w-8 h-8 rounded-lg bg-red-50 border border-red-100 flex items-center justify-center">
            <svg
              className="w-4 h-4 text-red-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2.5}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </div>
          <div>
            <h3 className="text-base font-bold text-surface-900 tracking-tight">
              Missing Keywords
            </h3>
            <p className="text-sm text-surface-500">
              {missingKeywords.length} not found — consider adding these
            </p>
          </div>
        </div>
        <div className="flex flex-wrap gap-2">
          {missingKeywords.length === 0 ? (
            <span className="text-sm text-surface-400 italic">No missing keywords!</span>
          ) : (
            missingKeywords.map((keyword, i) => (
              <span key={i} className="pill-red">
                {keyword}
              </span>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
