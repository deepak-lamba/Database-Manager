import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentPage: string = 'login'; // Initial page

  setCurrentPage(page: string) {
    this.currentPage = page;
  }
  // Define similar methods for other components/pages
}
