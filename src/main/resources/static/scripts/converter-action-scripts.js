// Function to reset the EP Token form
function cancelEpForm() {
    const tokenInput = document.getElementById("epToken");
    tokenInput.value = '';
    tokenInput.classList.remove("is-invalid"); // Reset invalid class if any
}

// Function to handle app domain selection, fetch apps, and toggle accordion panels
function handleAppDomainSelection(selectedAppDomainButton) {

    // Call the function to fetch applications for the selected domain
    getAppsForAppDomainId(selectedAppDomainButton);

    // Close the Application Domain accordion (Step 2)
    const appDomainAccordion = document.getElementById('collapseOne');
    if (appDomainAccordion) {
        appDomainAccordion.classList.remove('show');  // Close accordion
    }

    // Open the Application List accordion (Step 3)
    const appListAccordion = document.getElementById('collapseTwo');
    if (appListAccordion) {
        appListAccordion.classList.add('show');  // Open accordion
    }

    document.querySelector('#headingOne .accordion-button').classList.remove('accordion-button-active');
    document.querySelector('#headingTwo .accordion-button').classList.add('accordion-button-active');

    // Scroll to the application list accordion for better user experience
    document.getElementById('headingTwo').scrollIntoView({behavior: 'smooth'});
}

// Function to handle app selection and toggle accordion panels
function handleApplicationSelection(selectedAppButton) {

    // Call the function to fetch versions for the selected application
    getVersionsForApplication(selectedAppButton);

    // Close the Application accordion (Step 3)
    const appListAccordion = document.getElementById('collapseTwo');
    if (appListAccordion) {
        appListAccordion.classList.remove('show');  // Close Step 3 accordion
    }

    // Open the Version accordion (Step 4)
    const versionAccordion = document.getElementById('collapseThree');
    if (versionAccordion) {
        versionAccordion.classList.add('show');  // Open Step 4 accordion
    }

    document.querySelector('#headingTwo .accordion-button').classList.remove('accordion-button-active');
    document.querySelector('#headingThree .accordion-button').classList.add('accordion-button-active');

    // Scroll to the version accordion for better user experience
    document.getElementById('headingThree').scrollIntoView({behavior: 'smooth'});
}

// Function to fetch applications for a selected Application Domain ID
function getAppsForAppDomainId(selectedAppDomainButton) {
    const appDomainId = selectedAppDomainButton.dataset.appDomainId;
    const appDomainName = selectedAppDomainButton.dataset.appDomainName;

    if (!appDomainId) {
        console.error("No App Domain ID found!");
        return;
    }

    displayLoader('.applicationList tbody'); // Display loader while fetching
    fetch(`/${appDomainId}/applications`)
        .then(handleErrors)
        .then(data => {
            populateTableForApplications('.applicationList tbody', data, generateAppRows, appDomainName, appDomainId);
        })
        .catch(error => {
            console.error("Error fetching apps:", error);
            showError('.applicationList tbody', 'Failed to load applications.');
        });
}

// Function to fetch versions for a selected Application ID
function getVersionsForApplication(selectedAppButton) {
    const appId = selectedAppButton.dataset.appId;
    const appDomainId = selectedAppButton.dataset.appDomainId;
    const appName = selectedAppButton.dataset.appName;
    const appDomainName = selectedAppButton.dataset.appDomainName;


    if (!appId || !appDomainId) {
        console.error("Application ID or App Domain ID is missing.");
        return;
    }

    displayLoader('.applicationVersionList tbody'); // Show loader while fetching
    fetch(`/${appDomainId}/applications/${appId}/versions`)
        .then(handleErrors)
        .then(data => {
            populateTableForApplicationVersions('.applicationVersionList tbody', data, generateVersionRows, appName, appId, appDomainId, appDomainName);
        })
        .catch(error => {
            console.error("Error fetching app versions:", error);
            showError('.applicationVersionList tbody', 'Failed to load versions.');
        });
}

// Function to download IS Artefact for a selected version
function generateISArtefactDownload(selectedAppVersionButton) {
    const appDomainId = selectedAppVersionButton.dataset.appDomainId;
    const appId = selectedAppVersionButton.dataset.appId;
    const appVersionId = selectedAppVersionButton.dataset.appVersionId;
    const appName = selectedAppVersionButton.dataset.appName;
    const appDomainName = selectedAppVersionButton.dataset.appDomainName;
    const appVersion = selectedAppVersionButton.dataset.appVersion;

    if (!appDomainId || !appId || !appVersionId) {
        console.error("Missing inputs for generating IS artefact.");
        return;
    }

    fetch(`/${appDomainId}/applications/${appId}/versions/${appVersionId}/isArtefact`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = appDomainName + '-' + appName + '-' + appVersion + '-ISArtefact.zip';

            // Append the link to the body and trigger the download
            document.body.appendChild(a);
            a.click();

            // Clean up
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error("Failed to get IS artefact for the selected Application version:", error);
        });
}

// Helper function to handle fetch errors
function handleErrors(response) {
    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    return response.json();
}

// Helper function to populate tables with application data
function populateTableForApplications(tableSelector, data, rowGenerator, appDomainName, appDomainId) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = ''; // Clear table content
    data.forEach((item, index) => {
        const row = rowGenerator(item, index, appDomainName, appDomainId);
        tableBody.insertAdjacentHTML('beforeend', row);
    });
}


// Helper function to populate tables with application version data
function populateTableForApplicationVersions(tableSelector, data, rowGenerator, appName, appId, appDomainId, appDomainName) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = ''; // Clear table content
    data.forEach((item, index) => {
        const row = rowGenerator(item, index, appName, appId, appDomainId, appDomainName);
        tableBody.insertAdjacentHTML('beforeend', row);
    });
}

// Helper function to generate rows for applications
function generateAppRows(app, index, appDomainName, appDomainId) {
    return `<tr>
                <td>${index + 1}</td>
                <td>${app.name}</td>
                <td>${app.numberOfVersions}</td>
                <td>
                    <button type="button" class="btn btn-primary" name="application" value="${app.id}"
                        data-app-id="${app.id}" 
                        data-app-domain-id="${appDomainId}"
                        data-app-name="${app.name}" 
                        data-app-domain-name="${appDomainName}"
                        onclick="handleApplicationSelection(this)">
                        Select
                    </button>
                </td>
            </tr>`;
}

// Helper function to generate rows for versions
function generateVersionRows(version, index, appName, appId, appDomainId, appDomainName) {
    //appName, appId, appDomainId, appDomainName
    return `<tr>
                <td>${index + 1}</td>
                <td>${version.version}</td>
                <td>${version.state}</td>
                <td><button class="btn btn-primary"
                            value="${version.id}"
                            data-app-version-id="${version.id}"
                            data-app-id="${appId}"
                            data-app-domain-id="${appDomainId}" 
                            data-app-domain-name="${appDomainName}"
                            data-app-name="${appName}"
                            data-app-version="${version.version}"
                            onclick="generateISArtefactDownload(this)">
                    Download
                </button></td>
            </tr>`;
}

// Show loader in the table body
function displayLoader(tableSelector) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = '<tr><td colspan="4" class="text-center">Loading...</td></tr>';
}

// Show error message in the table
function showError(tableSelector, message) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">${message}</td></tr>`;
}

function goBackToStep(stepCounter) {
    // Close all accordion panels
    const accordions = document.querySelectorAll('.accordion-collapse');
    accordions.forEach(acc => acc.classList.remove('show'));

    // Open the specified step accordion
    const targetAccordion = document.getElementById(`collapse${stepCounter}`);
    if (targetAccordion) {
        targetAccordion.classList.add('show');  // Open the target accordion
    }

    document.querySelectorAll('.accordion-button').forEach(button => button.classList.remove('accordion-button-active'));
    document.querySelector(`#heading${stepCounter} .accordion-button`).classList.add('accordion-button-active');

    // Scroll to the target accordion for better user experience
    document.getElementById(`heading${stepCounter}`).scrollIntoView({behavior: 'smooth'});
}

function toggleDescription(button) {
    const shortDescription = button.previousElementSibling; // Get the short description
    const fullDescription = shortDescription.nextElementSibling; // Get the full description

    // Toggle visibility
    if (fullDescription.classList.contains('d-none')) {
        // Show full description
        fullDescription.classList.remove('d-none');
        shortDescription.classList.add('d-none');
        button.textContent = 'Hide'; // Change button text to "Hide"
    } else {
        // Hide full description
        fullDescription.classList.add('d-none');
        shortDescription.classList.remove('d-none');
        button.textContent = 'Show'; // Change button text back to "Show"
    }
}
