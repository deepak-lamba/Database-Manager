import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {
	
  requesterId: string = '';
  requesterName: string = '';
  query: string = '';
  comments: string = '';
  approverNameId: string = '';
  approverId: string = '';
  approverName: string = '';
  userList: any[] = []; // Assuming userList is an array of user objects [{ id: 1, name: 'User1' }, { id: 2, name: 'User2' }, ...]
  selectedUser: string = ''; // Variable to store the selected user's ID
  constructor(private apiService: ApiService) { }

  ngOnInit(){
    // Fetch user list from backend API
    this.apiService.getuserList().subscribe(
      response => {
    	this.userList = JSON.parse(response.body.userList);
        this.requesterId = response.body.user_id;
        this.requesterName = response.body.user_name;
      },
      error => {
        console.error('Error fetching user list:', error);
      }
    );
  }
  
  onUserSelected(event: any) {
	  // Find the user object with the selected ID
	  const selectedUserId: string = event.target.value;
	  const selectedUser = this.userList.find(user => user.id === selectedUserId);
	  // Store the selected user's ID and name
	  this.approverId = selectedUserId;
	  this.approverName = selectedUser.name;
	}
  
  submitRequest(): void {
    // Prepare request data
    const requestData = {
      requester_name: this.requesterName,
      requester_user_id: this.requesterId,
      query: this.query,
      comments: this.comments,
      approver_name: this.approverName,
      approver_user_id: this.approverId,
      status: "Pending"
    };

    // Call API service method to submit the request
    this.apiService.submitRequest(requestData).subscribe(
      response => {
        console.log('Request submitted successfully:', response);
        // Optionally, display success message to user
      },
      error => {
        console.error('Error submitting request:', error);
        // Optionally, display error message to user
      }
    );
  }
}
