// script.js

function cancelEpForm() {
    document.getElementById("epToken").value = '';
}

function getAppsForAppDomainId(selectedAppDomainRadio) {
    const appDomainId = selectedAppDomainRadio.dataset.appDomainId;
    const appDomainName = selectedAppDomainRadio.dataset.appDomainName;
    if (appDomainId) {
        fetch(`/${appDomainId}/applications`)
            .then(response => response.json())
            .then(data => {
                const tableBody = document.querySelector('.applicationTable tbody');
                tableBody.innerHTML = ''; // Clear existing rows
                data.forEach((app, index) => {
                    const row = `<tr>
                                <td>${index + 1}</td>
                                <td>${app.name}</td>
                                <td>${app.numberOfVersions}</td>
                                <td><input type="radio" name="application" 
                                            value="${app.id}" 
                                            data-app-id="${app.id}" 
                                            data-app-domain-id="${appDomainId}" 
                                            data-app-name="${app.name}" 
                                            data-app-domain-name="${appDomainName}" 
                                    onclick="getVersionsForApplication(this)"></td>
                            </tr>`;
                    tableBody.insertAdjacentHTML('beforeend', row);
                });
                document.querySelector('.applicationTable').style.display = 'block';
                const appDomainNamePara = document.querySelector('.EpAppDomainNameText');
                appDomainNamePara.textContent = 'Displaying EP applications for Application Domain : ' + `${appDomainName}`;
            })
            .catch(error => {
                console.error("Error fetching apps:", error);
            });
    } else {
        console.error("Failed to get appDomainId from selected radio button.");
    }
}

function getVersionsForApplication(selectedAppRadio) {
    const appId = selectedAppRadio.dataset.appId;
    const appDomainId = selectedAppRadio.dataset.appDomainId;
    const appName = selectedAppRadio.dataset.appName;
    const appDomainName = selectedAppRadio.dataset.appDomainName;

    console.log("App domain id:" + appDomainId);
    console.log("App id:" + appId);

    if (appDomainId && appId) {
        fetch(`/${appDomainId}/applications/${appId}/versions`)
            .then(response => response.json())
            .then(data => {
                console.log("App Versions for appId", appId, ":", data);
                const tableBody = document.querySelector('.applicationVersionTable tbody');
                tableBody.innerHTML = '';
                data.forEach((appVersion, index) => {
                    const row = `<tr>
                                <td>${index + 1}</td>
                                <td>${appVersion.version}</td>
                                <td>${appVersion.state}</td>
                                <td><button class="btn btn-primary" value="${appVersion.id}" 
                                        data-app-version-id="${appVersion.id}" 
                                        data-app-id="${appId}" 
                                        data-app-domain-id="${appDomainId}" 
                                        data-app-domain-name="${appDomainName}"
                                        data-app-name="${appName}"
                                        data-app-version="${appVersion.version}"
                                    onclick="generateISArtefactDownload(this)">Download</button></td>
                            </tr>`;
                    tableBody.insertAdjacentHTML('beforeend', row);
                });
                document.querySelector('.applicationVersionTable').style.display = 'block';

                const appNamePara = document.querySelector('.EPAppNameText');
                appNamePara.textContent = 'Displaying versions for Application : ' + `${appName}`;
            })
            .catch(error => {
                console.error("Error fetching app versions:", error);
            });
    } else {
        console.error("Failed to get applicationId from selected radio button.");
    }
}


function generateISArtefactDownload(selectedAppVersionButton) {
    const appId = selectedAppVersionButton.dataset.appId;
    const appDomainId = selectedAppVersionButton.dataset.appDomainId;
    const appVersionId = selectedAppVersionButton.dataset.appVersionId;
    const appVersion = selectedAppVersionButton.dataset.appVersion;
    const appName = selectedAppVersionButton.dataset.appName;
    const appDomainName = selectedAppVersionButton.dataset.appDomainName;

    console.log("App domain id:" + appDomainId);
    console.log("App id:" + appId);
    console.log("App Version id:" + appVersionId);

    if (appDomainId && appId && appVersionId) {
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
    } else {
        console.error("Failed to get required inputs from selected button element.");
    }
}