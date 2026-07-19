import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DocumentService } from '../services/document.service';
import { TranslatePipe } from '../utils/translate.pipe';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [RouterLink, DatePipe, TranslatePipe],
  templateUrl: './history.html',
  styleUrl: '../upload/upload.css'
})
export class HistoryComponent implements OnInit {
  documents: any[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private documentService: DocumentService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadHistory();
  }

  loadHistory() {
    this.isLoading = true;
    this.documentService.getHistory().subscribe({
      next: (data) => {
        this.documents = Array.isArray(data) ? data : [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Geçmiş analizler yüklenemedi.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteAnalysis(id: string) {
    if (confirm('Are you sure you want to delete this analysis?')) {
      this.documentService.deleteDocument(id).subscribe({
        next: () => {
          this.loadHistory();
        },
        error: (err) => {
          alert('Failed to delete the document: ' + err.message);
        }
      });
    }
  }
}
