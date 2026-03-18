import { notFound } from "next/navigation";
import Link from "next/link";
import { bootcampApi } from "@/lib/bootcampApi";
import BootcampEditForm from "@/components/bootcamp/BootcampEditForm";
import { ApiError } from "@/lib/api";
import type { Metadata } from "next";

interface PageProps {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const { id: slug } = await params;
  try {
    const res = await bootcampApi.getBootcampBySlug(slug);
    return { title: `${res.data?.name ?? "부트캠프"} 수정` };
  } catch {
    return { title: "부트캠프 수정" };
  }
}

export default async function EditBootcampPage({ params }: PageProps) {
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
    <div className="mx-auto max-w-2xl px-4 py-12">
      <Link
        href={`/bootcamps/${bootcamp.slug}`}
        className="mb-8 inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-900"
      >
        ← 상세 페이지로
      </Link>
      <h1 className="mb-8 mt-4 text-2xl font-bold text-gray-900">부트캠프 수정</h1>
      <BootcampEditForm bootcamp={bootcamp} />
    </div>
  );
}
