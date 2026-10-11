import React, { createContext, useContext, useState, useEffect } from 'react';

const translations = {
  en: {
    appTitle: 'EduTrack',
    dashboard: 'Dashboard',
    attendance: 'Attendance',
    marks: 'Marks & Performance',
    bunkCalculator: 'Bunk Calculator',
    leavePortal: 'Leave Portal',
    disputes: 'Disputes & Corrections',
    calendar: 'Academic Calendar',
    atRiskScore: 'At-Risk Score',
    parentPortal: 'Parent Portal',
    settings: 'Settings',
    searchPlaceholder: 'Search students, teachers, subjects... (Ctrl+K)',
    signOut: 'Sign Out',
    save: 'Save Changes',
    cancel: 'Cancel',
    loading: 'Loading data...',
    present: 'Present',
    absent: 'Absent',
    late: 'Late',
    leave: 'Leave',
    qrCheckin: 'QR Check-in',
    startQrSession: 'Start QR Session',
    scanQrCode: 'Scan QR Code',
    geofence: 'Geofence Enabled',
    atRiskTitle: 'Academic & Attendance Risk Indicator',
    lowRisk: 'Low Risk',
    mediumRisk: 'Medium Risk',
    highRisk: 'High Risk'
  },
  te: {
    appTitle: 'ఎడ్యుట్రాక్',
    dashboard: 'డాష్‌బోర్డ్',
    attendance: 'హాజరు',
    marks: 'మార్కులు & ప్రదర్శన',
    bunkCalculator: 'బంక్ క్యాలిక్యులేటర్',
    leavePortal: 'లీవ్ పోర్టల్',
    disputes: 'వివాదాలు & సవరణలు',
    calendar: 'అకాడమిక్ క్యాలెండర్',
    atRiskScore: 'ఎట్-రిస్క్ స్కోర్',
    parentPortal: 'తల్లిదండ్రుల పోర్టల్',
    settings: 'సెట్టింగ్‌లు',
    searchPlaceholder: 'విద్యార్థులు, ఉపాధ్యాయులను శోధించండి... (Ctrl+K)',
    signOut: 'లాగ్ అవుట్',
    save: 'మార్పులను సేవ్ చేయండి',
    cancel: 'రద్దు చేయండి',
    loading: 'డేటా లోడ్ అవుతోంది...',
    present: 'హాజరు',
    absent: 'గైర్హాజరు',
    late: 'ఆలస్యం',
    leave: 'సెలవు',
    qrCheckin: 'QR చెక్-ఇన్',
    startQrSession: 'QR సెషన్ ప్రారంభించండి',
    scanQrCode: 'QR కోడ్‌ని స్కాన్ చేయండి',
    geofence: 'జియోఫెన్స్ ప్రారంభించబడింది',
    atRiskTitle: 'అకాడమిక్ & హాజరు రిస్క్ సూచిక',
    lowRisk: 'తక్కువ రిస్క్',
    mediumRisk: 'మధ్యస్థ రిస్క్',
    highRisk: 'అధిక రిస్క్'
  },
  hi: {
    appTitle: 'एड्यूट्रैक',
    dashboard: 'डैशबोर्ड',
    attendance: 'उपस्थिति',
    marks: 'अंक और प्रदर्शन',
    bunkCalculator: 'बंक कैलकुलेटर',
    leavePortal: 'छुट्टी पोर्टल',
    disputes: 'विवाद और संशोधन',
    calendar: 'अकादमिक कैलेंडर',
    atRiskScore: 'एट-रिस्क स्कोर',
    parentPortal: 'अभिभावक पोर्टल',
    settings: 'सेटिंग्स',
    searchPlaceholder: 'छात्रों, शिक्षकों, विषयों को खोजें... (Ctrl+K)',
    signOut: 'साइन आउट',
    save: 'परिवर्तन सहेजें',
    cancel: 'रद्द करें',
    loading: 'डेटा लोड हो रहा है...',
    present: 'उपस्थित',
    absent: 'अनुपस्थित',
    late: 'देर से',
    leave: 'छुट्टी',
    qrCheckin: 'QR चेक-इन',
    startQrSession: 'QR सत्र शुरू करें',
    scanQrCode: 'QR कोड स्कैन करें',
    geofence: 'जियोफेंस सक्षम',
    atRiskTitle: 'अकादमिक और उपस्थिति जोखिम संकेतक',
    lowRisk: 'कम जोखिम',
    mediumRisk: 'मध्यम जोखिम',
    highRisk: 'उच्च जोखिम'
  }
};

const LanguageContext = createContext();

export const LanguageProvider = ({ children }) => {
  const [language, setLanguageState] = useState(() => {
    return localStorage.getItem('edutrack_lang') || 'en';
  });

  const setLanguage = (lang) => {
    if (translations[lang]) {
      setLanguageState(lang);
      localStorage.setItem('edutrack_lang', lang);
    }
  };

  const t = (key) => {
    return translations[language]?.[key] || translations.en[key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => useContext(LanguageContext);
