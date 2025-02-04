# Panduan Sistem Absensi dengan Pengenalan Wajah

Sistem absensi modern dengan fitur pengenalan wajah dan manajemen PKL (Praktik Kerja Lapangan) untuk sekolah kejuruan.

## Daftar Isi
1. [Fitur Utama](#fitur-utama)
2. [Panduan Pengguna](#panduan-pengguna)
3. [Instalasi dan Konfigurasi](#instalasi-dan-konfigurasi)
4. [Troubleshooting](#troubleshooting)
5. [FAQ](#faq)

## Fitur Utama

### 1. Pengenalan Wajah
- **Registrasi Wajah**
  - Daftarkan wajah siswa untuk absensi
  - Verifikasi keakuratan data wajah
  - Pembaruan data wajah jika diperlukan

- **Absensi dengan Wajah**
  - Absensi masuk/keluar otomatis
  - Deteksi wajah real-time
  - Validasi lokasi untuk memastikan kehadiran

### 2. Manajemen PKL
- **Penempatan PKL**
  - Pendaftaran tempat PKL
  - Penugasan pembimbing
  - Penjadwalan PKL

- **Aktivitas Harian**
  - Pencatatan kegiatan harian
  - Upload foto kegiatan
  - Persetujuan pembimbing

### 3. Pelaporan
- **Laporan Absensi**
  - Rekap kehadiran harian
  - Statistik keterlambatan
  - Export data ke Excel/PDF

- **Laporan PKL**
  - Progress PKL siswa
  - Evaluasi pembimbing
  - Dokumentasi kegiatan

## Panduan Pengguna

### Untuk Siswa

#### 1. Registrasi dan Login
1. Buka aplikasi di browser
2. Klik "Daftar" untuk membuat akun baru
3. Isi data diri dengan lengkap
4. Tunggu verifikasi dari admin

#### 2. Registrasi Wajah
1. Login ke akun Anda
2. Pilih menu "Registrasi Wajah"
3. Ikuti panduan untuk foto wajah
4. Pastikan:
   - Pencahayaan cukup
   - Wajah terlihat jelas
   - Tidak menggunakan masker/kacamata hitam

#### 3. Absensi Harian
1. Buka menu "Absensi"
2. Pilih "Check-in" atau "Check-out"
3. Arahkan kamera ke wajah
4. Tunggu verifikasi sistem
5. Cek status absensi

#### 4. Kegiatan PKL
1. Akses menu "PKL"
2. Isi jurnal harian
3. Upload foto kegiatan
4. Tunggu persetujuan pembimbing

### Untuk Guru/Pembimbing

#### 1. Monitoring Absensi
1. Login sebagai guru
2. Akses dashboard absensi
3. Lihat statistik kehadiran
4. Generate laporan

#### 2. Supervisi PKL
1. Buka menu "Supervisi PKL"
2. Pilih siswa bimbingan
3. Review jurnal harian
4. Berikan persetujuan/catatan

### Untuk Admin

#### 1. Manajemen Pengguna
1. Akses panel admin
2. Kelola data siswa/guru
3. Atur hak akses
4. Reset password

#### 2. Konfigurasi Sistem
1. Pengaturan jam kerja
2. Konfigurasi toleransi keterlambatan
3. Manajemen lokasi absensi
4. Backup data

## Instalasi dan Konfigurasi

### Persyaratan Sistem
- Java 17 atau lebih tinggi
- PostgreSQL 14 atau lebih tinggi
- Maven 3.8 atau lebih tinggi
- OpenCV 4.x

### Langkah Instalasi
1. Clone repository:
   ```bash
   git clone https://github.com/yourusername/attendance-system.git
   cd attendance-system
   ```

2. Konfigurasi database:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/attendance_db
   spring.datasource.username=postgres
   spring.datasource.password=password
   ```

3. Build project:
   ```bash
   mvn clean install
   ```

4. Jalankan aplikasi:
   ```bash
   mvn spring-boot:run
   ```

## Troubleshooting

### Masalah Umum

#### 1. Gagal Registrasi Wajah
- Pastikan pencahayaan cukup
- Cek kualitas kamera
- Jangan gunakan foto/gambar

#### 2. Error Absensi
- Periksa koneksi internet
- Aktifkan GPS
- Pastikan berada di lokasi yang ditentukan

#### 3. Masalah Login
- Clear cache browser
- Reset password jika lupa
- Hubungi admin untuk bantuan

## FAQ

### Umum
Q: Bagaimana jika lupa password?
A: Gunakan fitur "Lupa Password" di halaman login

Q: Apakah bisa absen dari rumah?
A: Tidak, sistem memvalidasi lokasi saat absensi

### Teknis
Q: Browser apa yang didukung?
A: Chrome, Firefox, Safari versi terbaru

Q: Berapa ukuran foto maksimal?
A: 10MB per foto

### PKL
Q: Kapan harus mengisi jurnal?
A: Setiap hari kerja sebelum jam 20:00

Q: Bagaimana jika lupa isi jurnal?
A: Hubungi pembimbing untuk pengisian manual

## Kontak Support

Jika mengalami masalah atau membutuhkan bantuan:
- Email: support@attendance-system.com
- Telepon: (021) 1234-5678
- WhatsApp: 0812-3456-7890

## Pembaruan Sistem

Sistem akan diperbarui secara berkala. Perubahan akan diumumkan melalui:
- Email
- Notifikasi aplikasi
- Grup WhatsApp

## Keamanan

- Ganti password secara berkala
- Jangan bagikan akun
- Logout setelah selesai
- Laporkan aktivitas mencurigakan

---

Dibuat dengan ❤️ oleh Tim Pengembang Sistem Absensi
Versi 1.0 - Februari 2025
