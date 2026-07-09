import { Component } from '@angular/core';
import { DocumentService } from '../services/document.service';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.html',
  standalone: true,
  imports: []
})
export class UploadComponent {
  selectedFile: File | null = null;
  analysisResult: any = null;

  constructor(private documentService: DocumentService) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  startAnalysis(): void {
    if (this.selectedFile) {
      console.log('Step 1: Sending file to backend (POST)...');

      this.documentService.uploadDocument(this.selectedFile).subscribe({
        next: (savedDocument: any) => {
          console.log('Step 2: File saved successfully. Document ID:', savedDocument.id);
          console.log('Step 3: Connecting to SSE stream to listen for AI results...');

          this.documentService.listenToAnalysis(savedDocument.id).subscribe({
            next: (aiResult: any) => {
              console.log('Step 4: AI Results received via SSE:', aiResult);
              this.analysisResult = typeof aiResult === 'string' ? JSON.parse(aiResult) : aiResult;
              alert('AI Analysis completed successfully!');
            },
            error: (streamErr: any) => {
              console.error('Error while streaming data:', streamErr);
              alert('Failed to receive analysis results.');
            }
          });
        },
        error: (uploadErr: any) => {
          console.error('File upload error: ', uploadErr);
          alert('Failed to upload file!');
        }
      });
    }
  }
}
