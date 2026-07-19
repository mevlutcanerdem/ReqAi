import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../services/document.service';
import { TranslatePipe } from '../utils/translate.pipe';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.html',
  styleUrl: './upload.css',
  standalone: true,
  imports: [CommonModule, TranslatePipe]
})
export class UploadComponent {
  selectedFile: File | null = null;
  analysisResult: any = null;

  constructor(private documentService: DocumentService, private cdr: ChangeDetectorRef) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }
isAnalyzing: boolean = false;
startAnalysis(): void {
  if (this.selectedFile) {
    this.isAnalyzing = true; // Yükleme başladı!
    this.cdr.detectChanges();

    this.documentService.uploadDocument(this.selectedFile).subscribe({
      next: (savedDocument: any) => {
        this.documentService.listenToAnalysis(savedDocument.id).subscribe({
          next: (aiResult: any) => {
            this.analysisResult = typeof aiResult === 'string' ? JSON.parse(aiResult) : aiResult;
            this.isAnalyzing = false; // Yükleme bitti!
            this.cdr.detectChanges();
          },
          error: (streamErr: any) => {
            console.error(streamErr);
            this.isAnalyzing = false; // Hata olsa da loading'i kapat
            this.cdr.detectChanges();
          }
        });
      },
      error: (uploadErr: any) => {
        console.error(uploadErr);
        this.isAnalyzing = false; // Hata olsa da loading'i kapat
        this.cdr.detectChanges();
      }
    });
  }
}
}
