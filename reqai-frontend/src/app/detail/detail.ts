import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router'; // <-- İŞTE BU SATIR EKSİKTİ
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [RouterLink], // Yukarıda import ettiğimiz için artık burası hata vermeyecek
  templateUrl: './detail.html'
})
export class DetailComponent implements OnInit {
  analysisDetail: any;

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`http://localhost:8081/api/v1/documents/${id}/analysis`)
      .subscribe(data => this.analysisDetail = data);
  }
}
