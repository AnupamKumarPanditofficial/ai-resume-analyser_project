// frontend/src/components/LoadingSpinner.tsx

import { useEffect, useState } from 'react';

const STATUS_MESSAGES = [
  'Extracting resume text...',
  'Running ATS analysis...',
  'Generating improvement suggestions...',
];

export default function LoadingSpinner() {
  const [messageIndex, setMessageIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setMessageIndex((prev) => (prev + 1) % STATUS_MESSAGES.length);
    }, 2500);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="flex flex-col items-center justify-center gap-8 py-20 animate-fade-in">
      <div className="relative">
        <div className="w-16 h-16 rounded-full border-4 border-surface-100" />
        <div className="absolute inset-0 w-16 h-16 rounded-full border-4 border-transparent border-t-primary-600 animate-spin" />
      </div>

      <div className="text-center">
        <p className="text-lg font-bold text-surface-900 tracking-tight transition-all duration-300">
          {STATUS_MESSAGES[messageIndex]}
        </p>
        <p className="text-sm text-surface-500 mt-2">
          This usually takes 10–20 seconds
        </p>
      </div>
    </div>
  );
}
