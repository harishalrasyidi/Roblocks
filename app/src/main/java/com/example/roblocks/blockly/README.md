# Blockly WebView Implementation

## Overview
Implementasi ini menciptakan pengalaman pemrograman blok berbasis web yang mulus di aplikasi Android menggunakan WebView. Pendekatan ini mengandalkan beberapa komponen utama:

1. **BlocklyWebView** - Widget WebView kustom yang dioptimalkan untuk menjalankan Blockly
2. **BlocklyActivity** - Aktivitas Android untuk menampilkan editor blok dengan UI yang sesuai
3. **HTML/JavaScript** - File HTML dengan JavaScript yang menjalankan Blockly di dalam WebView

## Struktur File Utama

### Kotlin
- `BlocklyWebView.kt` - Kelas WebView kustom untuk Blockly dengan pengaturan dan fungsi pendukung 
- `BlocklyActivity.kt` - Aktivitas yang menampilkan editor blok dalam UI Android
- `BlockEditorScreen.kt` - Layar Compose yang meluncurkan aktivitas native

### XML Layout
- `activity_blockly.xml` - Layout untuk BlocklyActivity dengan container WebView dan kontrol

### HTML/JavaScript (Assets)
- `blockly_editor.html` - HTML/JS untuk Blockly dengan dukungan offline dan fallback
- `iot_toolbox.xml` - Definisi toolbox dengan kategori dan blok untuk IoT
- `iot_blocks.json` - Definisi blok kustom (opsional)

## Fitur Utama
- **Web & Native Integration**: Komunikasi dua arah antara WebView dan kode native menggunakan JavaScript Interface
- **Offline Support**: Sistem fallback untuk situasi tanpa koneksi internet
- **Error Handling**: Log dan status yang kaya untuk debugging
- **Workspace Management**: Kemampuan menyimpan dan me-load workspace
- **Toolbox Customization**: Konfigurasi toolbox yang fleksibel

## Alur Kerja Dasar
1. BlocklyActivity dibuat dan menyiapkan UI
2. BlocklyWebView diinisialisasi dengan pengaturan yang tepat
3. HTML Blockly dimuat dari assets
4. Toolbox dikonfigurasi dan editor diinisialisasi
5. User dapat mengedit blok, perubahan dipantau melalui callbacks
6. Kode dapat dibuat dan dikirim ke Android dari JavaScript

## Debugging
- Log terstruktur di kedua sisi (Java/Kotlin dan JavaScript)
- Status visual dapat ditemukan dalam UI
- Error handling yang tepat pada semua lapisan

## Poin Penting
- Komunikasi antara JavaScript dan Kotlin menggunakan `addJavascriptInterface` dan callbacks
- WebViewClient diperlukan untuk menangani loading dan error
- WebChromeClient diperlukan untuk konsol JavaScript dan dialog
- Perlu mengaktifkan JavaScript, DOM storage, dan izin lain di WebView
- Untuk keamanan, semua URL ditampilkan di dalam aplikasi

## Catatan Keamanan
- JavaScript diizinkan hanya dari sumber tepercaya (assets lokal)
- Validasi masukan dilakukan untuk data yang dilewatkan antara Android dan JavaScript
- Pengaitan file dan akses eksternal dibatasi secara ketat

## Solusi Masalah Umum
- **WebView Blank**: Pastikan file HTML benar, JavaScript diaktifkan, dan paths benar
- **JavaScript Error**: Periksa log konsol, pastikan library dimuat dengan benar
- **Toolbox Missing**: Validasi format XML dan pastikan dimuat setelah Blockly diinisialisasi
- **Loading Issues**: Pastikan callback onPageFinished dipanggil sebelum operasi Blockly 