import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './detail.html'
})
export class DetailComponent implements OnInit {
  analysisDetail: any = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`http://localhost:8081/api/v1/documents/${id}/analysis`)
      .subscribe({
        next: (data) => {
          console.log("Backendden gelen ham veri:", data); // Verinin tipini konsolda görelim

          let parsedData = typeof data === 'string' ? JSON.parse(data) : data;

          // Eğer backend doğrudan bir array (dizi) dönüyorsa, onu objeye saralım
          if (Array.isArray(parsedData)) {
            this.analysisDetail = { requirements: parsedData };
          } else {
            this.analysisDetail = parsedData;
          }

          this.cdr.detectChanges();
        },
        error: (err) => console.error("Detay çekilirken hata:", err)
      });
  }
}
