// frontend/src/components/AnalyzeButton.tsx

interface AnalyzeButtonProps {
  isValid: boolean;
  isLoading: boolean;
  onClick: () => void;
}

/**
 * CTA button that triggers the analysis.
 * Disabled and grayed out until both inputs are valid (JD ≥ 50 chars AND PDF selected).
 */
export default function AnalyzeButton({
  isValid,
  isLoading,
  onClick,
}: AnalyzeButtonProps) {
  return (
    <button
      id="analyze-button"
      onClick={onClick}
      disabled={!isValid || isLoading}
      className="btn-primary w-full flex items-center justify-center gap-3 text-base"
    >
      {isLoading ? (
        <>
          <svg
            className="w-5 h-5 animate-spin"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
            />
          </svg>
          Analyzing...
        </>
      ) : (
        <>
          <svg
            className="w-5 h-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.455 2.456L21.75 6l-1.036.259a3.375 3.375 0 00-2.455 2.456z"
            />
          </svg>
          Analyze Resume
        </>
      )}
    </button>
  );
}
