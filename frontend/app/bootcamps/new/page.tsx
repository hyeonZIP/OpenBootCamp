import type { Metadata } from "next";
import BootcampForm from "@/components/bootcamp/BootcampForm";

export const metadata: Metadata = {
  title: "부트캠프 등록",
};

export default function NewBootcampPage() {
  return (
    <div className="mx-auto max-w-2xl px-4 py-12">
      <h1 className="mb-8 text-2xl font-bold text-gray-900">부트캠프 등록</h1>
      <BootcampForm />
    </div>
  );
}
