"use client";

import { useState, useId, isValidElement, Children, cloneElement } from "react";
import { useRouter } from "next/navigation";
import { bootcampApi } from "@/lib/bootcampApi";
import {
  TRACK_TYPE_LABEL,
  OPERATION_TYPE_LABEL,
  type TrackType,
  type OperationType,
  type TechStack,
  type BootcampTrackRequest,
} from "@/lib/types";
import { ApiError } from "@/lib/api";

const ALL_TRACK_TYPES = Object.keys(TRACK_TYPE_LABEL) as TrackType[];
const ALL_OPERATION_TYPES = Object.keys(OPERATION_TYPE_LABEL) as OperationType[];

const TECH_STACK_OPTIONS: TechStack[] = [
  "JAVA", "KOTLIN", "PYTHON", "NODE_JS", "GO",
  "SPRING_BOOT", "DJANGO", "FASTAPI", "EXPRESS", "NEST_JS",
  "JAVASCRIPT", "TYPESCRIPT", "REACT", "NEXT_JS", "VUE", "ANGULAR",
  "ANDROID", "IOS", "REACT_NATIVE", "FLUTTER",
  "MYSQL", "POSTGRESQL", "MONGODB", "REDIS",
  "DOCKER", "KUBERNETES", "AWS", "GCP",
  "PYTORCH", "TENSORFLOW",
];

interface TrackForm {
  trackType: TrackType;
  operationType: OperationType;
  techStacks: TechStack[];
  priceMin: string;
  priceMax: string;
  durationWeeks: string;
  isRecruiting: boolean;
}

const emptyTrack = (): TrackForm => ({
  trackType: "BACKEND",
  operationType: "ONLINE",
  techStacks: [],
  priceMin: "",
  priceMax: "",
  durationWeeks: "",
  isRecruiting: false,
});

export default function BootcampForm() {
  const router = useRouter();

  const [name, setName] = useState("");
  const [englishName, setEnglishName] = useState("");
  const [logoUrl, setLogoUrl] = useState("");
  const [description, setDescription] = useState("");
  const [officialUrl, setOfficialUrl] = useState("");
  const [tracks, setTracks] = useState<TrackForm[]>([]);

  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const addTrack = () => setTracks((prev) => [...prev, emptyTrack()]);

  const removeTrack = (index: number) =>
    setTracks((prev) => prev.filter((_, i) => i !== index));

  const updateTrack = (index: number, patch: Partial<TrackForm>) =>
    setTracks((prev) =>
      prev.map((t, i) => (i === index ? { ...t, ...patch } : t))
    );

  const toggleTechStack = (index: number, stack: TechStack) => {
    const track = tracks[index];
    if (track.techStacks.includes(stack)) {
      updateTrack(index, { techStacks: track.techStacks.filter((s) => s !== stack) });
    } else {
      updateTrack(index, { techStacks: [...track.techStacks, stack] });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);
    setSubmitting(true);

    const trackRequests: BootcampTrackRequest[] = tracks.map((t) => ({
      trackType: t.trackType,
      operationType: t.operationType,
      techStacks: t.techStacks,
      priceMin: t.priceMin ? parseInt(t.priceMin, 10) : undefined,
      priceMax: t.priceMax ? parseInt(t.priceMax, 10) : undefined,
      durationWeeks: t.durationWeeks ? parseInt(t.durationWeeks, 10) : undefined,
      isRecruiting: t.isRecruiting,
    }));

    try {
      const res = await bootcampApi.createBootcamp({
        name,
        englishName,
        logoUrl: logoUrl || undefined,
        description: description || undefined,
        officialUrl: officialUrl || undefined,
        tracks: trackRequests,
      });
      router.push(`/bootcamps/${res.data?.slug}`);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrorMsg(err.message);
      } else {
        setErrorMsg("등록 중 오류가 발생했습니다.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      {/* 기본 정보 */}
      <section className="flex flex-col gap-4 rounded-xl border border-gray-200 p-5">
        <h2 className="text-sm font-semibold text-gray-700">기본 정보</h2>

        <Field label="부트캠프 이름 *">
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="예) 위코드 부트캠프"
            className={inputClass}
          />
        </Field>

        <Field label="영문명 (URL 슬러그용)">
          <input
            type="text"
            required
            value={englishName}
            onChange={(e) => setEnglishName(e.target.value)}
            placeholder="예) wecode"
            className={inputClass}
          />
          <p className="text-xs text-gray-400">
            부트캠프 URL에 사용됩니다. 예) /bootcamps/wecode · 영문자, 숫자, 공백만 입력하세요.
          </p>
        </Field>

        <Field label="로고 URL">
          <input
            type="url"
            value={logoUrl}
            onChange={(e) => setLogoUrl(e.target.value)}
            placeholder="https://example.com/logo.png"
            className={inputClass}
          />
        </Field>

        <Field label={`소개 (${description.length}/500)`}>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={500}
            rows={3}
            placeholder="부트캠프를 간략히 소개해주세요."
            className={inputClass}
          />
        </Field>

        <Field label="공식 홈페이지 URL">
          <input
            type="url"
            value={officialUrl}
            onChange={(e) => setOfficialUrl(e.target.value)}
            placeholder="https://example.com"
            className={inputClass}
          />
        </Field>
      </section>

      {/* 트랙 */}
      <section className="flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold text-gray-700">트랙</h2>
          <button
            type="button"
            onClick={addTrack}
            className="rounded-lg border border-gray-300 px-3 py-1.5 text-xs font-medium text-gray-600 hover:bg-gray-50"
          >
            + 트랙 추가
          </button>
        </div>

        {tracks.length === 0 && (
          <p className="rounded-xl border border-dashed border-gray-200 py-8 text-center text-xs text-gray-400">
            트랙을 추가하면 교육 과정 상세 정보를 입력할 수 있습니다.
          </p>
        )}

        {tracks.map((track, i) => (
          <div key={i} className="flex flex-col gap-4 rounded-xl border border-gray-200 p-5">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-gray-500">트랙 {i + 1}</span>
              <button
                type="button"
                onClick={() => removeTrack(i)}
                className="text-xs text-red-400 hover:text-red-600"
              >
                삭제
              </button>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <Field label="트랙 유형 *">
                <select
                  value={track.trackType}
                  onChange={(e) => updateTrack(i, { trackType: e.target.value as TrackType })}
                  className={inputClass}
                >
                  {ALL_TRACK_TYPES.map((t) => (
                    <option key={t} value={t}>{TRACK_TYPE_LABEL[t]}</option>
                  ))}
                </select>
              </Field>

              <Field label="운영 방식 *">
                <select
                  value={track.operationType}
                  onChange={(e) => updateTrack(i, { operationType: e.target.value as OperationType })}
                  className={inputClass}
                >
                  {ALL_OPERATION_TYPES.map((t) => (
                    <option key={t} value={t}>{OPERATION_TYPE_LABEL[t]}</option>
                  ))}
                </select>
              </Field>

              <Field label="최소 수강료 (원)">
                <input
                  type="number"
                  min={0}
                  value={track.priceMin}
                  onChange={(e) => updateTrack(i, { priceMin: e.target.value })}
                  placeholder="0"
                  className={inputClass}
                />
              </Field>

              <Field label="최대 수강료 (원)">
                <input
                  type="number"
                  min={0}
                  value={track.priceMax}
                  onChange={(e) => updateTrack(i, { priceMax: e.target.value })}
                  placeholder="0"
                  className={inputClass}
                />
              </Field>

              <Field label="교육 기간 (주)">
                <input
                  type="number"
                  min={1}
                  value={track.durationWeeks}
                  onChange={(e) => updateTrack(i, { durationWeeks: e.target.value })}
                  placeholder="12"
                  className={inputClass}
                />
              </Field>

              <Field label="모집 중">
                <label className="flex h-9 cursor-pointer items-center gap-2">
                  <input
                    type="checkbox"
                    checked={track.isRecruiting}
                    onChange={(e) => updateTrack(i, { isRecruiting: e.target.checked })}
                    className="h-4 w-4 rounded border-gray-300"
                  />
                  <span className="text-sm text-gray-700">현재 모집 중</span>
                </label>
              </Field>
            </div>

            <Field label="기술 스택">
              <div className="flex flex-wrap gap-2">
                {TECH_STACK_OPTIONS.map((stack) => {
                  const selected = track.techStacks.includes(stack);
                  return (
                    <button
                      key={stack}
                      type="button"
                      onClick={() => toggleTechStack(i, stack)}
                      className={`rounded-md px-2.5 py-1 text-xs font-medium transition ${
                        selected
                          ? "bg-gray-900 text-white"
                          : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                      }`}
                    >
                      {stack}
                    </button>
                  );
                })}
              </div>
            </Field>
          </div>
        ))}
      </section>

      {/* 에러 */}
      {errorMsg && (
        <p className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{errorMsg}</p>
      )}

      {/* 제출 */}
      <div className="flex gap-3">
        <button
          type="button"
          onClick={() => router.back()}
          className="flex-1 rounded-lg border border-gray-300 py-2.5 text-sm font-medium text-gray-600 hover:bg-gray-50"
        >
          취소
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="flex-1 rounded-lg bg-gray-900 py-2.5 text-sm font-medium text-white hover:bg-gray-700 disabled:opacity-50"
        >
          {submitting ? "등록 중..." : "등록"}
        </button>
      </div>
    </form>
  );
}

const inputClass =
  "w-full rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-gray-500 focus:ring-1 focus:ring-gray-500";

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  const id = useId();
  const childArray = Children.toArray(children);
  const first = childArray[0];
  const linkedFirst =
    isValidElement(first) &&
    typeof first.type === "string" &&
    ["input", "select", "textarea"].includes(first.type)
      ? cloneElement(first as React.ReactElement<React.HTMLAttributes<HTMLElement>>, { id })
      : first;
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={id} className="text-xs font-medium text-gray-500">{label}</label>
      {linkedFirst}
      {childArray.slice(1)}
    </div>
  );
}
