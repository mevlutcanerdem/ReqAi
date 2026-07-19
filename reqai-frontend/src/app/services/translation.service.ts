import { Injectable, signal } from '@angular/core';

export type SupportedLanguage = 'en' | 'tr';

const TRANSLATIONS = {
  en: {
    'nav.new_analysis': '+ New Analysis',
    'nav.history': 'History Dashboard',
    'nav.logout': 'Logout',
    
    'upload.title': 'Start a New AI Document Analysis',
    'upload.subtitle': 'Upload your requirement document and let our Enterprise AI do the heavy lifting.',
    'upload.btn_start': '🚀 Start AI Analysis',
    'upload.loading': '🧠 AI is processing your document...',
    'upload.loading_sub': 'This may take a few seconds depending on document length.',
    'upload.error_title': '❌ Analysis Failed',
    'upload.error_sub': 'Please check backend logs or try again.',
    'upload.results_title': '🚀 Enterprise AI Analysis Results',
    'upload.priority': '⚡ Priority:',
    'upload.complexity': '🧠 Complexity:',
    'upload.dev_tasks': '👨‍💻 Developer Tasks',
    'upload.test_scenarios': '🧪 Test Scenarios',
    'upload.action': 'Action:',
    'upload.expect': 'Expect:',
    
    'history.back': '← Back to History',
    'history.title': 'Past Analyses',
    'history.found': 'Found',
    'history.btn_new': '+ New Analysis',
    'history.col_name': 'Document Name',
    'history.col_date': 'Upload Date',
    'history.col_action': 'Action',
    'history.btn_view': 'View Details',
    'history.btn_delete': 'Delete',
    'history.loading': 'Loading history... ⏳',
    'history.loading_sub': 'Fetching your past analyses',
    
    'detail.loading': 'Loading analysis details... ⏳',
    'detail.loading_sub': 'Fetching data from the server'
  },
  tr: {
    'nav.new_analysis': '+ Yeni Analiz',
    'nav.history': 'Geçmiş Paneli',
    'nav.logout': 'Çıkış',
    
    'upload.title': 'Yeni Yapay Zeka Belge Analizi Başlat',
    'upload.subtitle': 'Gereksinim belgenizi yükleyin ve kurumsal yapay zekamız işi halletsin.',
    'upload.btn_start': '🚀 AI Analizini Başlat',
    'upload.loading': '🧠 Yapay Zeka belgenizi işliyor...',
    'upload.loading_sub': 'Belge uzunluğuna bağlı olarak bu birkaç saniye sürebilir.',
    'upload.error_title': '❌ Analiz Başarısız',
    'upload.error_sub': 'Lütfen arka uç loglarını kontrol edin veya tekrar deneyin.',
    'upload.results_title': '🚀 Kurumsal Yapay Zeka Analiz Sonuçları',
    'upload.priority': '⚡ Öncelik:',
    'upload.complexity': '🧠 Karmaşıklık:',
    'upload.dev_tasks': '👨‍💻 Geliştirici Görevleri',
    'upload.test_scenarios': '🧪 Test Senaryoları',
    'upload.action': 'Eylem:',
    'upload.expect': 'Beklenen:',
    
    'history.back': '← Geçmişe Dön',
    'history.title': 'Geçmiş Analizler',
    'history.found': 'Bulundu',
    'history.btn_new': '+ Yeni Analiz',
    'history.col_name': 'Belge Adı',
    'history.col_date': 'Yükleme Tarihi',
    'history.col_action': 'İşlem',
    'history.btn_view': 'Detayları Gör',
    'history.btn_delete': 'Sil',
    'history.loading': 'Geçmiş yükleniyor... ⏳',
    'history.loading_sub': 'Geçmiş analizleriniz getiriliyor',
    
    'detail.loading': 'Analiz detayları yükleniyor... ⏳',
    'detail.loading_sub': 'Sunucudan veriler getiriliyor'
  }
};

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  currentLang = signal<SupportedLanguage>('en');

  setLanguage(lang: SupportedLanguage) {
    this.currentLang.set(lang);
    localStorage.setItem('reqai_lang', lang);
  }

  getLanguage() {
    return this.currentLang();
  }

  translate(key: string): string {
    const lang = this.currentLang();
    const dictionary = TRANSLATIONS[lang] as any;
    return dictionary[key] || key;
  }

  constructor() {
    const saved = localStorage.getItem('reqai_lang') as SupportedLanguage;
    if (saved && (saved === 'en' || saved === 'tr')) {
      this.currentLang.set(saved);
    }
  }
}
