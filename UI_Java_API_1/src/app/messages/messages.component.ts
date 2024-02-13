import { Component } from '@angular/core';
import { ApiService } from '../api.service';

interface Message {
	  id: number;
	  comments: string | null;
	  time_stamp: string | null;
	  approver_user_id: number;
	  approver_name: string | null;
	  query: string;
	  requester_user_id: number;
	  requester_name: string | null;
	  status: string | null;
	  newStatus: string; // Optional property for new status input field
	  newComment: string; // Optional property for new comment input field
	}

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrl: './messages.component.css'
})

export class MessagesComponent {
	
	ngOnInit(): void {
	    // Fetch messages when the component is initialized
	    if (this.currentPage === 'messages') {
	      this.message();
	    }
	  }
	
	currentPage: string = 'messages'; // Initial page
	logsMessage: Message[] = [];
	  setCurrentPage(page: string) {
	    this.currentPage = page;
	  }
	
	constructor(private apiService: ApiService) { }
	
	logsMyMessage: Message[] = [];
	
	message() {
		  this.apiService.getMessages().subscribe(
		    response => {
		    	this.logsMessage = response.body;
		   // Initialize newStatus and newComment fields for each message
		         this.logsMessage.forEach((message: Message) => {
		          message.newStatus = ''; // New status input field
		          message.newComment = message.comments + '--------------------------' + 'Add your Comment in Line below'; // New comment input field
		        });
		    },
		    error => {
		      console.error('Error fetching Messages:', error);
		    }
		  );
		}
	
	
	 updateMessage(message: any) {
		    // Update the message with new status and comment
		    const updatedMessage = {
		      id: message.id,
		      status: message.newStatus,
		      comments: message.newComment
		    };

		    // Call API service method to update message
		    this.apiService.updateMessage(updatedMessage).subscribe(
		      response => {
		        console.log('Message updated:', response);
		      },
		      error => {
		        console.error('Error updating Message:', error);
		      }
		    );
		  }
	
	
	mymessage() {
		  this.apiService.getMyMessages().subscribe(
		    response => {
		    	this.logsMyMessage = response.body; // Assuming response.body is the array of messages
		    	this.logsMyMessage.forEach((message: Message) => {
		          console.log('Message:', message);
		        });
		    },
		    error => {
		      console.error('Error fetching Your Messages:', error);
		    }
		  );
		}
	
	logsResults: any[] = [];
	operation(query: string){
		this.apiService.execute_operations(query).subscribe(
				
				response => {
					this.logsResults = response.body;
					this.logsResults.forEach((message: any) => {
						 console.log('Query Output:', message);
						 });
				},
				error => {
					 console.error('Error fetching Messages:', error);
				}
				);
				
	}
}
