// frontend/src/components/ScoreGauge.tsx

import { useEffect, useRef, useState } from 'react';

interface ScoreGaugeProps {
  score: number;
  analysisId: string;
}

export default function ScoreGauge({ score, analysisId }: ScoreGaugeProps) {
  const [animated, setAnimated] = useState(false);
  const circleRef = useRef<SVGCircleElement>(null);

  const size = 180;
  const strokeWidth = 14;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference * (1 - score / 100);

  const getColor = (s: number) => {
    if (s < 50) return '#DC2626'; // red-600
    if (s < 75) return '#D97706'; // amber-600
    return '#059669'; // emerald-600
  };

  const getLabel = (s: number) => {
    if (s < 50) return 'Needs Work';
    if (s < 75) return 'Good Match';
    return 'Excellent';
  };

  const color = getColor(score);

  useEffect(() => {
    const timer = requestAnimationFrame(() => setAnimated(true));
    return () => cancelAnimationFrame(timer);
  }, []);

  return (
    <div className="flex flex-col items-center gap-5 animate-fade-in">
      <div className="relative">
        <svg
          width={size}
          height={size}
          viewBox={`0 0 ${size} ${size}`}
          className="transform -rotate-90"
        >
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            fill="none"
            stroke="#F4F4F5" /* surface-100 */
            strokeWidth={strokeWidth}
          />
          <circle
            ref={circleRef}
            cx={size / 2}
            cy={size / 2}
            r={radius}
            fill="none"
            stroke={color}
            strokeWidth={strokeWidth}
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={animated ? offset : circumference}
            className="gauge-animate"
          />
        </svg>

        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span
            className="text-5xl font-black tracking-tighter tabular-nums"
            style={{ color }}
          >
            {score}
          </span>
          <span className="text-xs font-bold text-surface-400 mt-1 tracking-widest uppercase">
            ATS Score
          </span>
        </div>
      </div>

      <span
        className="text-sm font-bold px-3 py-1 rounded-md"
        style={{
          backgroundColor: `${color}15`,
          color: color,
        }}
      >
        {getLabel(score)}
      </span>

      <span className="text-xs text-surface-400 font-mono bg-surface-50 px-2 py-1 rounded border border-surface-200">
        ID: {analysisId ? analysisId.substring(0, 8) : 'N/A'}
      </span>
    </div>
  );
}
