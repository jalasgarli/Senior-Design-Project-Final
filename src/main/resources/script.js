
const fileInput = document.getElementById('file-upload');

// Add change event listener to handle file upload
fileInput.addEventListener('change', (event) => {
    const uploadedFile = event.target.files[0];
    if (uploadedFile) {
        // File is uploaded, update button appearance
        const button = event.target.previousElementSibling; // Get the button
        button.style.backgroundColor = '#adb5bd'; // Change background color
        button.textContent = 'UPLOADED'; // Change text content
        // Maintain container size
    }
});

const generateButton = document.getElementById('generate-button');

// Add click event listener to handle button click
generateButton.addEventListener('click', () => {
    // Change the background color to the hover color
    generateButton.style.backgroundColor = '#45a049';

    // Change the button text content to 'UPLOADED'
    generateButton.textContent = 'Generating the file...';

    // Prevent the container size from changing

});


function handleGenerateSchedule() {
    // Get references to the file inputs and room occupation percentage input
    const roomFileInput = document.getElementById('room-csv');
    const studentFileInput = document.getElementById('course-csv');
    const roomOccupationInput = document.getElementById('room-occupation');

    // Check if both files are selected
    if (!roomFileInput.files[0] || !studentFileInput.files[0]) {
        alert('Please select both room and student files.');
        return;
    }

    // Prepare form data to send to the backend
    const formData = new FormData();
    formData.append('roomFile', roomFileInput.files[0]);
    formData.append('studentFile', studentFileInput.files[0]);
    formData.append('percent', roomOccupationInput.value/100); // Include roomOccupation

    // Send a POST request to the backend API
    fetch('http://localhost:8080/schedule-exam', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to schedule exam.');
            }
            return response.text();
        })
        .then(result => {
            alert(result); // Display success message
            console.log(31)
            // Start checking for the generated file periodically
            checkForGeneratedFile();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while scheduling the exam.');
        });
}

function checkForGeneratedFile() {
    // Define the interval to check for the file (e.g., every 5 seconds)
    const interval = setInterval(() => {
        // Send a GET request to the backend API to check for the generated file
        fetch('http://localhost:8080/download')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to download file.');
                }
                console.log(50)
                return response.blob();
            })
            .then(blob => {
                console.log(54)

                // If the file is ready, initiate download and clear the interval
                if (blob.size > 0) {
                    clearInterval(interval);
                    downloadFile(blob);
                }
            })
            .catch(error => {
                console.log(63)
                console.error('Error:', error);
                clearInterval(interval); // Clear interval on error
                alert('An error occurred while downloading the file.');
            });
    }, 5000); // Adjust the interval as needed (e.g., every 5 seconds)
}

function downloadFile(blob) {
    // Create a URL for the blob and initiate download
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'output.csv'; // Specify the file name
    document.body.appendChild(a); // Append anchor element to body
    a.click(); // Trigger download
    a.remove(); // Remove anchor element
    window.URL.revokeObjectURL(url); // Release the object URL
}




