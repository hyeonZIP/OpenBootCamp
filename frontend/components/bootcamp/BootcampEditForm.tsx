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
  type BootcampResponse,
  type BootcampTrackResponse,
  type BootcampTrackRequest,
} from "@/lib/types";
import { TrackTypeBadge, OperationTypeBadge } from "./TrackBadge";
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

interface TrackFormState {
  trackType: TrackType;
  operationType: OperationType;
  techStacks: TechStack[];
  priceMin: string;
  priceMax: string;
  durationWeeks: string;
  isRecruiting: boolean;
}

const emptyTrackForm = (): TrackFormState => ({
  trackType: "BACKEND",
  operationType: "ONLINE",
  techStacks: [],
  priceMin: "",
  priceMax: "",
  durationWeeks: "",
  isRecruiting: false,
});

const trackToForm = (track: BootcampTrackResponse): TrackFormState => ({
  trackType: track.trackType,
  operationType: track.operationType,
  techStacks: [...track.techStacks],
  priceMin: track.priceMin !== null ? String(track.priceMin) : "",
  priceMax: track.priceMax !== null ? String(track.priceMax) : "",
  durationWeeks: track.durationWeeks !== null ? String(track.durationWeeks) : "",
  isRecruiting: track.isRecruiting ?? false,
});

const formToRequest = (form: TrackFormState): BootcampTrackRequest => ({
  trackType: form.trackType,
  operationType: form.operationType,
  techStacks: form.techStacks,
  priceMin: form.priceMin ? parseInt(form.priceMin, 10) : undefined,
  priceMax: form.priceMax ? parseInt(form.priceMax, 10) : undefined,
  durationWeeks: form.durationWeeks ? parseInt(form.durationWeeks, 10) : undefined,
  isRecruiting: form.isRecruiting,
});

interface Props {
  bootcamp: BootcampResponse;
}

export default function BootcampEditForm({ bootcamp }: Props) {
  const router = useRouter();

  // 기본 정보 폼 상태
  const [name, setName] = useState(bootcamp.name);
  const [englishName, setEnglishName] = useState(bootcamp.englishName ?? "");
  const [logoUrl, setLogoUrl] = useState(bootcamp.logoUrl ?? "");
  const [description, setDescription] = useState(bootcamp.description ?? "");
  const [officialUrl, setOfficialUrl] = useState(bootcamp.officialUrl ?? "");

  // 트랙 목록 상태
  const [tracks, setTracks] = useState<BootcampTrackResponse[]>(bootcamp.tracks);

  // 트랙 편집 상태: null=없음, "new"=추가 중, number=해당 trackId 수정 중
  const [editingTrackId, setEditingTrackId] = useState<"new" | number | null>(null);
  const [trackForm, setTrackForm] = useState<TrackFormState>(emptyTrackForm());

  // 로딩 상태
  const [submitting, setSubmitting] = useState(false);
  const [trackSubmitting, setTrackSubmitting] = useState(false);
  const [deletingTrackId, setDeletingTrackId] = useState<number | null>(null);

  // 에러 상태
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [trackErrorMsg, setTrackErrorMsg] = useState<string | null>(null);

  // ── 기본 정보 저장 ──────────────────────────────────────────────
  const handleSaveBasicInfo = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);
    setSubmitting(true);
    try {
      const res = await bootcampApi.updateBootcamp(bootcamp.id, {
        name,
        englishName,
        logoUrl: logoUrl || undefined,
        description: description || undefined,
        officialUrl: officialUrl || undefined,
      });
      if (!res.data?.slug) {
        setErrorMsg("수정은 완료됐지만 서버에서 slug를 반환하지 않았습니다.");
        return;
      }
      router.push(`/bootcamps/${res.data.slug}`);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrorMsg(err.message);
      } else {
        setErrorMsg("수정 중 오류가 발생했습니다.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  // ── 트랙 추가 ──────────────────────────────────────────────────
  const handleAddTrack = async () => {
    setTrackErrorMsg(null);
    setTrackSubmitting(true);
    try {
      const res = await bootcampApi.addTrack(bootcamp.id, formToRequest(trackForm));
      if (res.data) {
        setTracks((prev) => [...prev, res.data!]);
        setEditingTrackId(null);
        setTrackForm(emptyTrackForm());
      } else {
        setTrackErrorMsg("트랙이 추가됐지만 서버에서 데이터를 반환하지 않았습니다.");
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setTrackErrorMsg(err.message);
      } else {
        setTrackErrorMsg("트랙 추가 중 오류가 발생했습니다.");
      }
    } finally {
      setTrackSubmitting(false);
    }
  };

  // ── 트랙 수정 ──────────────────────────────────────────────────
  const handleUpdateTrack = async (trackId: number) => {
    setTrackErrorMsg(null);
    setTrackSubmitting(true);
    try {
      const res = await bootcampApi.updateTrack(bootcamp.id, trackId, formToRequest(trackForm));
      if (res.data) {
        setTracks((prev) => prev.map((t) => (t.id === trackId ? res.data! : t)));
        setEditingTrackId(null);
      } else {
        setTrackErrorMsg("트랙이 수정됐지만 서버에서 데이터를 반환하지 않았습니다.");
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setTrackErrorMsg(err.message);
      } else {
        setTrackErrorMsg("트랙 수정 중 오류가 발생했습니다.");
      }
    } finally {
      setTrackSubmitting(false);
    }
  };

  // ── 트랙 삭제 ──────────────────────────────────────────────────
  const handleDeleteTrack = async (trackId: number) => {
    setDeletingTrackId(trackId);
    setTrackErrorMsg(null);
    try {
      await bootcampApi.deleteTrack(bootcamp.id, trackId);
      setTracks((prev) => prev.filter((t) => t.id !== trackId));
      if (editingTrackId === trackId) {
        setEditingTrackId(null);
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setTrackErrorMsg(err.message);
      } else {
        setTrackErrorMsg("트랙 삭제 중 오류가 발생했습니다.");
      }
    } finally {
      setDeletingTrackId(null);
    }
  };

  const startEditTrack = (track: BootcampTrackResponse) => {
    setTrackForm(trackToForm(track));
    setEditingTrackId(track.id);
    setTrackErrorMsg(null);
  };

  const startAddTrack = () => {
    setTrackForm(emptyTrackForm());
    setEditingTrackId("new");
    setTrackErrorMsg(null);
  };

  const cancelTrackEdit = () => {
    setEditingTrackId(null);
    setTrackErrorMsg(null);
  };

  const toggleTechStack = (stack: TechStack) => {
    setTrackForm((prev) => ({
      ...prev,
      techStacks: prev.techStacks.includes(stack)
        ? prev.techStacks.filter((s) => s !== stack)
        : [...prev.techStacks, stack],
    }));
  };

  return (
    <div className="flex flex-col gap-6">
      {/* ── 기본 정보 ── */}
      <form
        onSubmit={handleSaveBasicInfo}
        className="flex flex-col gap-4 rounded-xl border border-gray-200 p-5"
      >
        <h2 className="text-sm font-semibold text-gray-700">기본 정보</h2>

        <Field label="부트캠프 이름 *">
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={inputClass}
          />
        </Field>

        <Field label="영문명 *">
          <input
            type="text"
            required
            value={englishName}
            onChange={(e) => setEnglishName(e.target.value)}
            placeholder="예) Wecode Pro"
            className={inputClass}
          />
          <div className="flex flex-col gap-0.5">
            <p className="text-xs text-gray-400">
              영문자, 숫자, 공백, 하이픈만 입력 가능합니다.
            </p>
            <p className="text-xs text-gray-500">
              URL 슬러그:{" "}
              {toSlugPreview(englishName) ? (
                <span className="font-mono font-medium text-gray-700">
                  /bootcamps/{toSlugPreview(englishName)}
                </span>
              ) : (
                <span className="text-gray-400">영문명을 입력하면 자동으로 생성됩니다</span>
              )}
            </p>
          </div>
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

        {errorMsg && (
          <p className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{errorMsg}</p>
        )}

        <div className="flex gap-3 pt-2">
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
            {submitting ? "저장 중..." : "기본 정보 저장"}
          </button>
        </div>
      </form>

      {/* ── 트랙 관리 ── */}
      <section className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold text-gray-700">
            트랙 관리 ({tracks.length}개)
          </h2>
          {editingTrackId === null && (
            <button
              type="button"
              onClick={startAddTrack}
              className="rounded-lg border border-gray-300 px-3 py-1.5 text-xs font-medium text-gray-600 hover:bg-gray-50"
            >
              + 트랙 추가
            </button>
          )}
        </div>

        {/* 트랙 전체 에러 메시지 */}
        {trackErrorMsg && (
          <p className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{trackErrorMsg}</p>
        )}

        {/* 기존 트랙 목록 */}
        {tracks.length === 0 && editingTrackId !== "new" && (
          <p className="rounded-xl border border-dashed border-gray-200 py-8 text-center text-xs text-gray-400">
            등록된 트랙이 없습니다.
          </p>
        )}

        {tracks.map((track) => (
          <div key={track.id} className="rounded-xl border border-gray-200">
            {/* 트랙 요약 행 */}
            <div className="flex items-center justify-between p-4">
              <div className="flex flex-wrap items-center gap-2">
                <TrackTypeBadge type={track.trackType} />
                <OperationTypeBadge type={track.operationType} />
                {track.isRecruiting && (
                  <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
                    모집 중
                  </span>
                )}
                {track.durationWeeks !== null && (
                  <span className="text-xs text-gray-500">{track.durationWeeks}주</span>
                )}
              </div>
              <div className="flex shrink-0 gap-3">
                <button
                  type="button"
                  onClick={() => {
                    if (editingTrackId === track.id) {
                      cancelTrackEdit();
                    } else {
                      startEditTrack(track);
                    }
                  }}
                  disabled={deletingTrackId === track.id}
                  className="text-xs font-medium text-blue-600 hover:text-blue-800 disabled:opacity-40"
                >
                  {editingTrackId === track.id ? "취소" : "수정"}
                </button>
                <button
                  type="button"
                  onClick={() => handleDeleteTrack(track.id)}
                  disabled={deletingTrackId === track.id || trackSubmitting}
                  className="text-xs font-medium text-red-500 hover:text-red-700 disabled:opacity-40"
                >
                  {deletingTrackId === track.id ? "삭제 중..." : "삭제"}
                </button>
              </div>
            </div>

            {/* 트랙 수정 폼 (인라인) */}
            {editingTrackId === track.id && (
              <div className="border-t border-gray-100 p-4">
                <TrackInlineForm
                  form={trackForm}
                  onChange={setTrackForm}
                  onToggleTechStack={toggleTechStack}
                  onSave={() => handleUpdateTrack(track.id)}
                  onCancel={cancelTrackEdit}
                  submitting={trackSubmitting}
                  saveLabel="수정 저장"
                />
              </div>
            )}
          </div>
        ))}

        {/* 새 트랙 추가 폼 */}
        {editingTrackId === "new" && (
          <div className="rounded-xl border border-gray-200 p-4">
            <p className="mb-4 text-xs font-semibold text-gray-500">새 트랙</p>
            <TrackInlineForm
              form={trackForm}
              onChange={setTrackForm}
              onToggleTechStack={toggleTechStack}
              onSave={handleAddTrack}
              onCancel={cancelTrackEdit}
              submitting={trackSubmitting}
              saveLabel="트랙 추가"
            />
          </div>
        )}
      </section>
    </div>
  );
}

// ── 인라인 트랙 폼 ────────────────────────────────────────────────────

interface TrackInlineFormProps {
  form: TrackFormState;
  onChange: (form: TrackFormState) => void;
  onToggleTechStack: (stack: TechStack) => void;
  onSave: () => void;
  onCancel: () => void;
  submitting: boolean;
  saveLabel: string;
}

function TrackInlineForm({
  form,
  onChange,
  onToggleTechStack,
  onSave,
  onCancel,
  submitting,
  saveLabel,
}: TrackInlineFormProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-2 gap-3">
        <Field label="트랙 유형 *">
          <select
            value={form.trackType}
            onChange={(e) => onChange({ ...form, trackType: e.target.value as TrackType })}
            className={inputClass}
          >
            {ALL_TRACK_TYPES.map((t) => (
              <option key={t} value={t}>{TRACK_TYPE_LABEL[t]}</option>
            ))}
          </select>
        </Field>

        <Field label="운영 방식 *">
          <select
            value={form.operationType}
            onChange={(e) => onChange({ ...form, operationType: e.target.value as OperationType })}
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
            value={form.priceMin}
            onChange={(e) => onChange({ ...form, priceMin: e.target.value })}
            placeholder="0"
            className={inputClass}
          />
        </Field>

        <Field label="최대 수강료 (원)">
          <input
            type="number"
            min={0}
            value={form.priceMax}
            onChange={(e) => onChange({ ...form, priceMax: e.target.value })}
            placeholder="0"
            className={inputClass}
          />
        </Field>

        <Field label="교육 기간 (주)">
          <input
            type="number"
            min={1}
            value={form.durationWeeks}
            onChange={(e) => onChange({ ...form, durationWeeks: e.target.value })}
            placeholder="12"
            className={inputClass}
          />
        </Field>

        <Field label="모집 중">
          <label className="flex h-9 cursor-pointer items-center gap-2">
            <input
              type="checkbox"
              checked={form.isRecruiting}
              onChange={(e) => onChange({ ...form, isRecruiting: e.target.checked })}
              className="h-4 w-4 rounded border-gray-300"
            />
            <span className="text-sm text-gray-700">현재 모집 중</span>
          </label>
        </Field>
      </div>

      <Field label="기술 스택">
        <div className="flex flex-wrap gap-2">
          {TECH_STACK_OPTIONS.map((stack) => {
            const selected = form.techStacks.includes(stack);
            return (
              <button
                key={stack}
                type="button"
                onClick={() => onToggleTechStack(stack)}
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

      <div className="flex gap-2">
        <button
          type="button"
          onClick={onCancel}
          disabled={submitting}
          className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 disabled:opacity-50"
        >
          취소
        </button>
        <button
          type="button"
          onClick={onSave}
          disabled={submitting}
          className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-700 disabled:opacity-50"
        >
          {submitting ? "저장 중..." : saveLabel}
        </button>
      </div>
    </div>
  );
}

// ── 공통 헬퍼 ────────────────────────────────────────────────────────

/** 백엔드 Slug.from() 로직과 동일하게 slug 미리보기 생성 */
function toSlugPreview(englishName: string): string {
  const normalized = englishName.normalize("NFD").replace(/[^\x00-\x7F]/g, "");
  const lower = normalized.toLowerCase().trim();
  const raw = lower.replace(/[^a-z0-9]+/g, "-");
  return raw.replace(/^-+|-+$/g, "");
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
