import Link from "next/link";
import Image from "next/image";
import type { BootcampResponse } from "@/lib/types";
import { TrackTypeBadge, OperationTypeBadge } from "./TrackBadge";

interface Props {
  bootcamp: BootcampResponse;
}

export default function BootcampCard({ bootcamp }: Props) {
  const { id, name, logoUrl, description, tracks } = bootcamp;

  return (
    <Link
      href={`/bootcamps/${id}`}
      className="flex flex-col rounded-xl border border-gray-200 bg-white p-5 transition hover:border-gray-400 hover:shadow-sm"
    >
      {/* 헤더 */}
      <div className="mb-3 flex items-center gap-3">
        {logoUrl ? (
          <Image
            src={logoUrl}
            alt={`${name} 로고`}
            width={40}
            height={40}
            className="rounded-lg object-contain"
          />
        ) : (
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gray-100 text-sm font-bold text-gray-500">
            {name.charAt(0)}
          </div>
        )}
        <h2 className="text-base font-semibold text-gray-900 line-clamp-1">{name}</h2>
      </div>

      {/* 설명 */}
      {description && (
        <p className="mb-4 text-sm text-gray-500 line-clamp-2">{description}</p>
      )}

      {/* 트랙 배지 */}
      {tracks.length > 0 && (
        <div className="mt-auto flex flex-wrap gap-1.5">
          {tracks.slice(0, 3).map((track) => (
            <span key={track.id} className="flex items-center gap-1">
              <TrackTypeBadge type={track.trackType} />
              <OperationTypeBadge type={track.operationType} />
            </span>
          ))}
          {tracks.length > 3 && (
            <span className="text-xs text-gray-400">+{tracks.length - 3}</span>
          )}
        </div>
      )}
    </Link>
  );
}
