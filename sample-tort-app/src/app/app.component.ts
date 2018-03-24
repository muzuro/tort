import {Component, OnInit} from '@angular/core';
import {University} from "./model/university";
import {UniversityService} from "./university.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  universities: University[] = [];
  from = 0;
  count = 10;
  title = 'Tort sample application';

  constructor(private universityService: UniversityService) { }

  ngOnInit(): void {
    this.getUniversities()
  }
  getUniversities(): void {
    this.universityService.getUniversities(this.from, this.count)
      .subscribe(universities => this.universities = universities)
  }

  next(): void {
    this.from += this.count
    this.getUniversities()
  }

  prev(): void {
    if (this.from == 0) {
      return
    }

    if (this.from - this.count < 0) {
      this.from = 0
    } else {
      this.from -= this.count
    }
    this.getUniversities()
  }

}
