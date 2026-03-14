import { OPERATION_TYPE_LABEL, TRACK_TYPE_LABEL } from "@/lib/types";
import type { OperationType, TrackType } from "@/lib/types";
import { cn } from "@/lib/utils";

const TRACK_COLOR: Record<TrackType, string> = {
  BACKEND: "bg-blue-100 text-blue-700",
  FRONTEND: "bg-purple-100 text-purple-700",
  FULLSTACK: "bg-indigo-100 text-indigo-700",
  MOBILE: "bg-green-100 text-green-700",
  DATA: "bg-yellow-100 text-yellow-700",
  DEVOPS: "bg-orange-100 text-orange-700",
  AI_ML: "bg-red-100 text-red-700",
  SECURITY: "bg-gray-200 text-gray-700",
  GAME: "bg-pink-100 text-pink-700",
};

export function TrackTypeBadge({ type }: { type: TrackType }) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
        TRACK_COLOR[type]
      )}
    >
      {TRACK_TYPE_LABEL[type]}
    </span>
  );
}

export function OperationTypeBadge({ type }: { type: OperationType }) {
  return (
    <span className="inline-flex items-center rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-600">
      {OPERATION_TYPE_LABEL[type]}
    </span>
  );
}
