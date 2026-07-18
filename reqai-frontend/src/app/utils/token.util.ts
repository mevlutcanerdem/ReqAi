const TOKEN_KEY = 'reqai_token';

export function getStoredToken(): string | null {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token || token === 'undefined' || token === 'null') {
    return null;
  }
  return token.trim();
}

export function saveStoredToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token.trim());
}

export function clearStoredToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

export function isTokenExpired(token: string): boolean {
  // Kalıcı API token'ları asla expire olmaz
  if (token.startsWith('reqai_')) {
    return false;
  }
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (!payload?.exp) {
      return true;
    }
    return payload.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
}

export function hasValidToken(): boolean {
  const token = getStoredToken();
  return !!token && !isTokenExpired(token);
}
