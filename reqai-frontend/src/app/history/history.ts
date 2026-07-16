import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // 1. EKLENDİ
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './history.html'
})
export class HistoryComponent implements OnInit {
  documents: any[] = [];

  // 2. ChangeDetectorRef constructor içine eklendi
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.http.get<any>('https://reqaiweb.onrender.com/api/v1/documents')
      .subscribe({
        next: (data) => {
          this.documents = typeof data === 'string' ? JSON.parse(data) : data;
          console.log("Tabloya basılacak veri adedi:", this.documents.length);

          // 3. İŞTE SİHİRLİ DEĞNEK: Angular'ı dürt ve ekranı çizdir!
          this.cdr.detectChanges();
        },
        error: (err) => console.error("Hata:", err)
      });
  }
}
