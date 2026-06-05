// frontend/src/components/JobDescriptionInput.tsx

import { useCallback } from 'react';

interface JobDescriptionInputProps {
  value: string;
  onChange: (value: string) => void;
  disabled: boolean;
}

export default function JobDescriptionInput({
  value,
  onChange,
  disabled,
}: JobDescriptionInputProps) {
  const charCount = value.length;
  const isValid = charCount >= 50 && charCount <= 5000;
  const isOverLimit = charCount > 5000;

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      onChange(e.target.value);
    },
    [onChange]
  );

  return (
    <div className="flex flex-col gap-3 h-full">
      <label
        htmlFor="job-description"
        className="flex items-center gap-2 text-sm font-semibold text-surface-900"
      >
        <svg
          className="w-4 h-4 text-surface-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
          />
        </svg>
        Job Description
      </label>

      <div className="relative flex-1 flex flex-col">
        <textarea
          id="job-description"
          value={value}
          onChange={handleChange}
          disabled={disabled}
          placeholder="Paste the full job description here (minimum 50 characters)...&#10;&#10;Include the role title, required skills, qualifications, and responsibilities for the most accurate analysis."
          className={`input-base flex-1 min-h-[300px] text-sm leading-relaxed scrollbar-thin ${
            disabled ? 'opacity-50 cursor-not-allowed bg-surface-50' : ''
          } ${
            isOverLimit
              ? 'border-red-300 focus:ring-red-500/20 focus:border-red-500'
              : isValid
              ? 'border-emerald-300 focus:ring-emerald-500/20 focus:border-emerald-500'
              : ''
          }`}
        />

        <div className="flex items-center justify-between mt-2 px-1">
          <span className="text-xs text-surface-500">
            {charCount < 50 && charCount > 0 && (
              <span className="text-amber-600">
                {50 - charCount} more characters needed
              </span>
            )}
            {charCount === 0 && 'Minimum 50 characters'}
            {isValid && !isOverLimit && (
              <span className="text-emerald-600">✓ Valid length</span>
            )}
            {isOverLimit && (
              <span className="text-red-600">Exceeds maximum length</span>
            )}
          </span>
          <span
            className={`text-xs font-mono ${
              isOverLimit
                ? 'text-red-600'
                : isValid
                ? 'text-emerald-600'
                : 'text-surface-500'
            }`}
          >
            {charCount.toLocaleString()} / 5,000
          </span>
        </div>
      </div>
    </div>
  );
}
