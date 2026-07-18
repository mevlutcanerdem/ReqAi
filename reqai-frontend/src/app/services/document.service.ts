import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getStoredToken } from '../utils/token.util';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private baseUrl = 'https://reqaiweb.onrender.com/api/v1/documents';

  constructor(private http: HttpClient) {}

  /**
   * Uploads the document file via standard POST request (Multipart FormData)
   * This is a non-blocking request that returns immediate document metadata and ID.
   */
  uploadDocument(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/upload`, formData);
  }

  getHistory(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  getAnalysisDetails(documentId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${documentId}/analysis`);
  }

  /**
   * Connects to the Server-Sent Events (SSE) stream using the document ID.
   * Listens asynchronously for the backend AI analysis result.
   */
  listenToAnalysis(documentId: string): Observable<any> {
    return new Observable((subscriber) => {
      const token = getStoredToken();
      const streamUrl = token
        ? `${this.baseUrl}/stream/${documentId}?token=${encodeURIComponent(token)}`
        : `${this.baseUrl}/stream/${documentId}`;
      const eventSource = new EventSource(streamUrl);

      // Listen for the specific custom event named 'analysis-result' fired by SseService
      eventSource.addEventListener('analysis-result', (event: MessageEvent) => {
        try {
          const parsedData = JSON.parse(event.data);
          subscriber.next(parsedData); // Forward the payload to the component

          // Crucial step: Close connections on both sides to prevent automatic reconnections (DDoS effect)
          eventSource.close();
          subscriber.complete();
        } catch (err) {
          subscriber.error('JSON parse error');
          eventSource.close();
        }
      });

      // Handle server crashes, timeouts, or network drops
      eventSource.onerror = (error) => {
        console.error('SSE connection error:', error);
        subscriber.error('Connection lost while listening to analysis.');
        eventSource.close();
      };
    });
  }
}
