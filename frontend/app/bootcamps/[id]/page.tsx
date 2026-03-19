import { notFound } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { bootcampApi } from "@/lib/bootcampApi";
import { TrackTypeBadge, OperationTypeBadge } from "@/components/bootcamp/TrackBadge";
import DeleteBootcampButton from "@/components/bootcamp/DeleteBootcampButton";
import type { Metadata } from "next";
import type { BootcampTrackResponse } from "@/lib/types";
import { ApiError } from "@/lib/api";

interface PageProps {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const { id: slug } = await params;
  try {
    const res = await bootcampApi.getBootcampBySlug(slug);
    return { title: res.data?.name ?? "부트캠프 상세" };
  } catch {
    return { title: "부트캠프 상세" };
  }
}

export default async function BootcampDetailPage({ params }: PageProps) {
  const { id: slug } = await params;

  let bootcamp = null;
  try {
    const res = await bootcampApi.getBootcampBySlug(slug);
    bootcamp = res.data;
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) {
      notFound();
    }
    throw err;
  }

  if (!bootcamp) notFound();

  return (
    <div className="mx-auto max-w-4xl px-4 py-12">
      {/* 뒤로가기 */}
      <Link
        href="/bootcamps"
        className="mb-8 inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-900"
      >
        ← 부트캠프 목록
      </Link>

      {/* 헤더 */}
      <div className="mb-8 flex items-start gap-5">
        {bootcamp.logoUrl ? (
          <Image
            src={bootcamp.logoUrl}
            alt={`${bootcamp.name} 로고`}
            width={64}
            height={64}
            className="rounded-xl object-contain"
          />
        ) : (
          <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-xl bg-gray-100 text-xl font-bold text-gray-500">
            {bootcamp.name.charAt(0)}
          </div>
        )}

        <div className="flex-1">
          <div className="flex items-start justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">{bootcamp.name}</h1>
              {bootcamp.officialUrl && (
                <a
                  href={bootcamp.officialUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="mt-1 inline-block text-sm text-blue-600 hover:underline"
                >
                  공식 홈페이지 →
                </a>
              )}
            </div>

            {/* 수정 / 삭제 버튼 */}
            <div className="flex shrink-0 gap-2">
              <Link
                href={`/bootcamps/${bootcamp.slug}/edit`}
                className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50"
              >
                수정
              </Link>
              <DeleteBootcampButton
                bootcampId={bootcamp.id}
                bootcampName={bootcamp.name}
              />
            </div>
          </div>

          {bootcamp.description && (
            <p className="mt-3 text-sm leading-relaxed text-gray-600">{bootcamp.description}</p>
          )}
        </div>
      </div>

      {/* 트랙 목록 */}
      <section>
        <h2 className="mb-4 text-lg font-semibold text-gray-900">
          트랙 ({bootcamp.tracks.length}개)
        </h2>

        {bootcamp.tracks.length === 0 ? (
          <div className="rounded-xl border border-dashed border-gray-300 py-12 text-center text-sm text-gray-400">
            등록된 트랙이 없습니다.
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {bootcamp.tracks.map((track) => (
              <TrackCard key={track.id} track={track} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

function TrackCard({ track }: { track: BootcampTrackResponse }) {
  const price =
    track.priceMin !== null && track.priceMax !== null
      ? `${track.priceMin.toLocaleString()}원 ~ ${track.priceMax.toLocaleString()}원`
      : track.priceMin !== null
      ? `${track.priceMin.toLocaleString()}원~`
      : track.priceMax !== null
      ? `~${track.priceMax.toLocaleString()}원`
      : null;

  return (
    <div className="rounded-xl border border-gray-200 p-5">
      {/* 상단 배지 */}
      <div className="mb-3 flex flex-wrap items-center gap-2">
        <TrackTypeBadge type={track.trackType} />
        <OperationTypeBadge type={track.operationType} />
        {track.isRecruiting && (
          <span className="rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700">
            모집 중
          </span>
        )}
      </div>

      {/* 상세 정보 */}
      <dl className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm sm:grid-cols-3">
        {price && (
          <div>
            <dt className="text-gray-400">수강료</dt>
            <dd className="font-medium text-gray-800">{price}</dd>
          </div>
        )}
        {track.durationWeeks !== null && (
          <div>
            <dt className="text-gray-400">교육 기간</dt>
            <dd className="font-medium text-gray-800">{track.durationWeeks}주</dd>
          </div>
        )}
      </dl>

      {/* 기술 스택 */}
      {track.techStacks.length > 0 && (
        <div className="mt-3 flex flex-wrap gap-1.5">
          {track.techStacks.map((stack) => (
            <span
              key={stack}
              className="rounded-md bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700"
            >
              {stack}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
