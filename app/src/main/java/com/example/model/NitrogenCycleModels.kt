package com.example.model

data class NitrogenCycleStep(
  val id: Int,
  val stepNumber: Int,
  val phaseLabel: String,
  val title: String,
  val description: String,
  val beginnerTip: String,
)

data class BeginnerWaterTip(
  val title: String,
  val detail: String,
)

val NITROGEN_CYCLE_STEPS =
  listOf(
    NitrogenCycleStep(
      id = 1,
      stepNumber = 1,
      phaseLabel = "Hari 1",
      title = "Siapkan Akuarium & Dekorasi",
      description =
        "Pasang pasir, batu, dan kayu, lalu isi air sesuai hasil volume bersih kalkulator. Jalankan filter dan tambahkan deklorinator untuk menetralkan klorin.",
      beginnerTip =
        "Gunakan nilai Volume Air Bersih dari kalkulator agar takaran obat anti-klorin lebih akurat.",
    ),
    NitrogenCycleStep(
      id = 2,
      stepNumber = 2,
      phaseLabel = "Hari 1–3",
      title = "Masukkan Sumber Amonia (NH₃)",
      description =
        "Mulai proses fishless cycling dengan menaburkan sedikit pakan ikan atau amonia murni hingga terbaca sekitar 2 ppm untuk memberi makan bakteri baik.",
      beginnerTip =
        "Jangan langsung memasukkan ikan sebelum siklus selesai agar ikan tidak keracunan amonia.",
    ),
    NitrogenCycleStep(
      id = 3,
      stepNumber = 3,
      phaseLabel = "Minggu 1–2",
      title = "Tumbuhkan Bakteri Pengurai Amonia",
      description =
        "Biarkan filter menyala 24 jam nonstop agar bakteri Nitrosomonas berkembang di media filter dan mulai mengubah amonia menjadi nitrit.",
      beginnerTip =
        "Jangan mencuci media filter biologis dengan air keran karena klorin dapat membunuh koloni bakteri.",
    ),
    NitrogenCycleStep(
      id = 4,
      stepNumber = 4,
      phaseLabel = "Minggu 2–4",
      title = "Pantau Lonjakan Nitrit (NO₂⁻)",
      description =
        "Lakukan tes air setiap 2–3 hari. Saat amonia mulai turun, kadar nitrit akan melonjak lalu perlahan turun karena bakteri Nitrobacter mulai aktif.",
      beginnerTip =
        "Anda bisa menambahkan bakteri starter cair untuk membantu mempercepat fase penumbuhan koloni.",
    ),
    NitrogenCycleStep(
      id = 5,
      stepNumber = 5,
      phaseLabel = "Minggu 4–6",
      title = "Nitrat (NO₃⁻) Terbentuk & Stabil",
      description =
        "Siklus dinyatakan matang ketika 2 ppm amonia dan nitrit habis terurai menjadi 0 ppm dalam waktu 24 jam, menyisakan nitrat.",
      beginnerTip =
        "Menanam tanaman air hidup sangat membantu menyerap nitrat berlebih secara alami.",
    ),
    NitrogenCycleStep(
      id = 6,
      stepNumber = 6,
      phaseLabel = "Siap Pakai",
      title = "Ganti Air Besar & Masukkan Ikan Bertahap",
      description =
        "Lakukan pergantian air 30%–50% untuk menurunkan kadar nitrat di bawah 20 ppm. Setelah suhu dan parameter stabil, masukkan ikan secara bertahap.",
      beginnerTip =
        "Masukkan sedikit ikan terlebih dahulu setiap minggu agar bakteri filter sempat beradaptasi.",
    ),
  )

val BEGINNER_NITROGEN_TIPS =
  listOf(
    BeginnerWaterTip(
      title = "Bersabar 4–6 Minggu",
      detail =
        "Siklus nitrogen alami membutuhkan waktu. Terburu-buru memasukkan ikan sering menyebabkan 'New Tank Syndrome'.",
    ),
    BeginnerWaterTip(
      title = "Filter Wajib Hidup 24 Jam",
      detail =
        "Bakteri baik membutuhkan aliran oksigen terus-menerus di media filter. Mematikan filter terlalu lama bisa mematikan bakteri.",
    ),
    BeginnerWaterTip(
      title = "Gunakan Test Kit Cair",
      detail =
        "Test kit cair (Amonia, Nitrit, Nitrat) jauh lebih akurat dibanding strip kertas untuk memantau selesainya siklus.",
    ),
    BeginnerWaterTip(
      title = "Cuci Media dengan Air Akuarium",
      detail =
        "Saat melakukan water change, bilas spons/bio-ring memakai air bekas kurasan akuarium, bukan air keran langsung.",
    ),
  )
