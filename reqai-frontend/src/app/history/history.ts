import { Component, OnInit } from '@angular/core';
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

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<any>('http://localhost:8081/api/v1/documents')
      .subscribe({
        next: (data) => {
          // Eğer veri string olarak geldiyse JSON objesine (diziye) çevir
          this.documents = typeof data === 'string' ? JSON.parse(data) : data;
          console.log("Tabloya basılacak veri adedi:", this.documents.length);
        },
        error: (err) => console.error("Hata:", err)
      });
  }
}
