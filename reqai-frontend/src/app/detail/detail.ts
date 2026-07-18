import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DocumentService } from '../services/document.service';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './detail.html'
})
export class DetailComponent implements OnInit {
  analysisDetail: any = null;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private documentService: DocumentService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }

    this.documentService.getAnalysisDetails(id).subscribe({
      next: (data) => {
        const parsedData = typeof data === 'string' ? JSON.parse(data) : data;
        this.analysisDetail = Array.isArray(parsedData)
          ? { requirements: parsedData }
          : parsedData;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Analiz detayları yüklenemedi veya bu belgeye erişim yetkiniz yok.';
        this.cdr.detectChanges();
      }
    });
  }
}
