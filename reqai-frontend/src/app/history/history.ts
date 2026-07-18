import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DocumentService } from '../services/document.service';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './history.html'
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
}
