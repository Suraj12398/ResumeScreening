document.getElementById("resumeFiles").addEventListener("change", handleFileSelection);

function handleFileSelection(event) {
    window.selectedFiles = event.target.files;
}

async function uploadFiles() {
    const files = window.selectedFiles;
    const mustKnowSkills = document.getElementById("mustKnowSkills").value.trim();
    const betterToKnowSkills = document.getElementById("betterToKnowSkills").value.trim();

    if (!files || files.length === 0) {
        alert("Please select resumes to upload.");
        return;
    }
    
    if (!mustKnowSkills && !betterToKnowSkills) {
        alert("Please enter at least one skill category.");
        return;
    }

    // Format the skills as expected by the backend
    const skills = `Must Know: ${mustKnowSkills}; Better to Know: ${betterToKnowSkills}`;

    let formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append("files", files[i]);
    }
    formData.append("skills", skills); // Send skills as a form field instead of query param

    const apiUrl = "http://localhost:8081/api/resume/upload-multiple";

    try {
        let response = await fetch(apiUrl, {
            method: "POST",
            body: formData,
        });

        if (!response.ok) {
            throw new Error("Failed to upload resumes.");
        }

        let data = await response.json();
        console.log(data);
        displayResults(data);
    } catch (error) {
        console.error("Error:", error);
        alert("Failed to upload resumes.");
    }
}

function resizeTextarea(textarea) {
    textarea.style.height = "auto";  // Reset height to recalculate
    textarea.style.height = textarea.scrollHeight + "px";  // Set new height
}

function displayResults(candidates) {
    const resultDiv = document.getElementById("result");

    let downloadBtn = document.getElementById("downloadReportBtn")

    resultDiv.innerHTML = "";

    if (!candidates || candidates.length === 0) {
        resultDiv.innerHTML = `<div class="no-results">No matching candidates found.</div>`;
        downloadBtn.style.display = "none"; // Hide button if no results
        return;
    }

    // Sorting the result
    candidates = candidates.sort(
        (a, b) => 
            (b.mustKnowSkillMatchCount + b.betterToKnowSkillMatchCount) - 
            (a.mustKnowSkillMatchCount + a.betterToKnowSkillMatchCount)
    );
    
    window.candidatesData = candidates; // Store for CSV download
    downloadBtn.style.display = "block"; // Show button when results are available
console.log(candidates)

   

    let html = `
        <table class="results-table">
            <thead>
                <tr>
                    <th>File Name</th>
                    <th>Must Know Score</th>
                    <th>Better to Know Score</th>
                    <th>Matched Must Know Skills</th>
                    <th>Matched Better to Know Skills</th>
                    <th>Total Score</th>
                   
                </tr>
            </thead>
            <tbody>
    `;

    

    candidates.forEach(candidate => {
        // Ensure properties exist before accessing
        const mustKnowMatched = candidate.mustKnowMatched ? Array.from(candidate.mustKnowMatched) : [];
        const betterToKnowMatched = candidate.betterToKnowMatched ? Array.from(candidate.betterToKnowMatched) : [];

        html += `
            <tr>
                <td>${candidate.fileName}</td>
                <td>${candidate.mustKnowSkillMatchCount ?? 0}</td>
                <td>${candidate.betterToKnowSkillMatchCount ?? 0}</td>
                <td>${candidate.mustKnowMatchedSkills.join(", ")}</td>
                <td>${candidate.betterToKnowMatchedSkills.join(", ")}</td>
                <td>${(candidate.betterToKnowSkillMatchCount ?? 0) +(candidate.mustKnowSkillMatchCount ?? 0)}</td>
                
            </tr>
        `;
    });

    html += `</tbody></table>`;
    resultDiv.innerHTML = html;
}


function downloadReport() {
    if (!window.candidatesData || window.candidatesData.length === 0) {
        alert("No data available to download.");
        return;
    }

    let csvContent = "File Name,Must Know Score,Better to Know Score,Matched Must Know Skills,Matched Better to Know Skills,Total\n";

    window.candidatesData.forEach(candidate => {
        const mustKnowMatched = candidate.mustKnowMatchedSkills ? Array.from(candidate.mustKnowMatchedSkills).join("; ") : "N/A";
        const betterToKnowMatched = candidate.betterToKnowMatchedSkills ? Array.from(candidate.betterToKnowMatchedSkills).join("; ") : "N/A";
        const total = (candidate.betterToKnowSkillMatchCount ?? 0) + (candidate.mustKnowSkillMatchCount ?? 0);

        // Ensuring CSV formatting by enclosing skills with commas in quotes
        const formattedMustKnowMatched = `"${mustKnowMatched}"`;
        const formattedBetterToKnowMatched = `"${betterToKnowMatched}"`;

        csvContent += `${candidate.fileName},${candidate.mustKnowSkillMatchCount ?? 0},${candidate.betterToKnowSkillMatchCount ?? 0},${formattedMustKnowMatched},${formattedBetterToKnowMatched},${total}\n`;
    });

    const blob = new Blob([csvContent], { type: "text/csv" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "Candidates_Report.csv";
    document.body.appendChild(link); // Required for Firefox
    link.click();
    document.body.removeChild(link);
}
