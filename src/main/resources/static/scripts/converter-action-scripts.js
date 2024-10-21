function cancelEpForm() {
    const tokenInput = document.getElementById("epToken");
    tokenInput.value = '';
    tokenInput.classList.remove("is-invalid");
}

document.addEventListener("DOMContentLoaded", function() {

    var errorFlag = /*[[${errorMessageFlag}]]*/ '';
    if (errorFlag === 'SESSION_EXPIRED') {
        var errorSection = document.getElementById('sessionExpiredError');
        if (errorSection) {
            errorSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
});

const AppDomainPagination = (function () {
    let appDomainsCurrentPage = 1;

    function fetchPaginatedAppDomains(pageNumber) {
        fetch(`/applicationDomains?pageNumber=${pageNumber}`)
            .then(handleErrors)
            .then(data => {
                appDomainsCurrentPage = data.currentPage;
                populateTableForApplicationDomains('.epObjectsTable tbody', data.applicationDomainDTOList);
                document.getElementById('appDomainsCurrentPage').textContent = appDomainsCurrentPage;
                document.getElementById('appDomainsNextPageBtn').setAttribute('data-app-domain-total-pages', data.totalPages);
                document.getElementById('appDomainsPrevPageBtn').disabled = appDomainsCurrentPage === 1;
                document.getElementById('appDomainsNextPageBtn').disabled = appDomainsCurrentPage === data.totalPages;
            })
            .catch(error => {
                console.error("Error fetching application domains:", error);
                showError('.epObjectsTable tbody', 'Failed to load application domains.');
            });
    }

    function loadAppDomainsNextPage(nextButton) {
        const appDomainsTotalPages = nextButton.dataset.appDomainTotalPages;
        if (appDomainsCurrentPage < appDomainsTotalPages) {
            fetchPaginatedAppDomains(appDomainsCurrentPage + 1);
        }
    }

    function loadAppDomainsPreviousPage() {
        if (appDomainsCurrentPage > 1) {
            fetchPaginatedAppDomains(appDomainsCurrentPage - 1);
        }
    }

    function populateTableForApplicationDomains(tableSelector, data) {
        const tableBody = document.querySelector(tableSelector);
        tableBody.innerHTML = '';

        data.forEach((applicationDomain, index) => {
            const row = `
            <tr>
                <td>${index + 1}</td>
                <td>${applicationDomain.name}</td>
                <td>
                    <span class="short-description">
                        ${abbreviateText(applicationDomain.description, 200)}
                    </span>
                    <span class="full-description d-none">
                        ${applicationDomain.description}
                    </span>
                    ${applicationDomain.description.length > 200 ? '<button type="button" class="btn btn-link p-0 show-more" onclick="toggleDescription(this)">Show</button>' : ''}
                </td>
                <td>
                    <button type="button" class="btn btn-primary" 
                        data-app-domain-id="${applicationDomain.id}" 
                        data-app-domain-name="${applicationDomain.name}"
                        onclick="handleAppDomainSelection(this)">
                        Select
                    </button>
                </td>
            </tr>`;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    }

    function abbreviateText(text, maxLength) {
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    }

    window.loadAppDomainsNextPage = loadAppDomainsNextPage;
    window.loadAppDomainsPreviousPage = loadAppDomainsPreviousPage;

    return {
        fetchPaginatedAppDomains, populateTableForApplicationDomains, abbreviateText
    };
})();


function handleAppDomainSelection(selectedAppDomainButton) {
    getAppsForAppDomainId(selectedAppDomainButton);
    const appDomainAccordion = document.getElementById('collapseOne');
    if (appDomainAccordion) {
        appDomainAccordion.classList.remove('show');
    }
    const appListAccordion = document.getElementById('collapseTwo');
    if (appListAccordion) {
        appListAccordion.classList.add('show');  // Open accordion
    }
    document.querySelector('#headingOne .accordion-button').classList.remove('accordion-button-active');
    document.querySelector('#headingTwo .accordion-button').classList.add('accordion-button-active');
    document.getElementById('headingTwo').scrollIntoView({behavior: 'smooth'});
}

function handleApplicationSelection(selectedAppButton) {
    getVersionsForApplication(selectedAppButton);
    const appListAccordion = document.getElementById('collapseTwo');
    if (appListAccordion) {
        appListAccordion.classList.remove('show');
    }
    const versionAccordion = document.getElementById('collapseThree');
    if (versionAccordion) {
        versionAccordion.classList.add('show');
    }
    document.querySelector('#headingTwo .accordion-button').classList.remove('accordion-button-active');
    document.querySelector('#headingThree .accordion-button').classList.add('accordion-button-active');
    document.getElementById('headingThree').scrollIntoView({behavior: 'smooth'});
}


function getAppsForAppDomainId(selectedAppDomainButton) {
    const appDomainId = selectedAppDomainButton.dataset.appDomainId;
    const appDomainName = selectedAppDomainButton.dataset.appDomainName;
    const appListPageNumber = 1;
    processAppListFetch(appDomainId, appDomainName, appListPageNumber);
}

function processAppListFetch(appDomainId, appDomainName, appListPageNumber) {
    if (!appDomainId || !appDomainName) {
        console.error("No App Domain ID or App Domain name found!");
        showError('.applicationList tbody', 'Failed to load applications.');
        return;
    }
    displayLoader('.applicationList tbody'); // Display loader while fetching
    fetch(`/${appDomainId}/applications?pageNumber=${appListPageNumber}`)
        .then(handleErrors)
        .then(data => {
            populateTableForApplications('.applicationList tbody', data.applicationDTOList, generateAppRows, appDomainName, appDomainId);
            updateAppsPagination(data.totalPages, data.currentPage, appDomainName, appDomainId);
        })
        .catch(error => {
            console.error("Error fetching apps:", error);
            showError('.applicationList tbody', 'Failed to load applications.');
        });
}

function loadAppsPage(paginationButton) {
    const appDomainId = paginationButton.dataset.appDomainId;
    const appDomainName = paginationButton.dataset.appDomainName;
    const appListTotalPages = parseInt(paginationButton.dataset.appsTotalPages, 10);
    const appListCurrentPage = parseInt(paginationButton.dataset.appsCurrentPage, 10);
    const direction = parseInt(paginationButton.dataset.direction, 10);
    const newPageNumber = appListCurrentPage + direction;
    (newPageNumber >= 1 && newPageNumber <= appListTotalPages) ? processAppListFetch(appDomainId, appDomainName, newPageNumber) : console.log("No more pages to fetch.");
}

function getVersionsForApplication(selectedAppButton) {
    const appId = selectedAppButton.dataset.appId;
    const appDomainId = selectedAppButton.dataset.appDomainId;
    const appName = selectedAppButton.dataset.appName;
    const appDomainName = selectedAppButton.dataset.appDomainName;
    const appVersionListPageNumber = 1;
    processAppVersionListFetch(appId, appDomainId, appName, appDomainName, appVersionListPageNumber);
}

function processAppVersionListFetch(appId, appDomainId, appName, appDomainName, appVersionListPageNumber) {
    if (!appId || !appDomainId) {
        console.error("Application ID or App Domain ID is missing.");
        return;
    }
    displayLoader('.applicationVersionList tbody'); // Show loader while fetching
    fetch(`/${appDomainId}/applications/${appId}/versions?pageNumber=${appVersionListPageNumber}`)
        .then(handleErrors)
        .then(data => {
            populateTableForApplicationVersions('.applicationVersionList tbody', data.applicationVersionDTOList, generateVersionRows, appName, appId, appDomainId, appDomainName);
            updateAppVersionPagination(data.totalPages, data.currentPage, appId, appDomainId, appName, appDomainName);
        })
        .catch(error => {
            console.error("Error fetching app versions:", error);
            showError('.applicationVersionList tbody', 'Failed to load versions.');
        });
}

function updateAppVersionPagination(totalAppVersionPagesCount, currentAppVersionPageNumber, appId, appDomainId, appName, appDomainName) {
    const currentPageElement = document.getElementById('appVersionsCurrentPage');
    const totalPagesElement = document.getElementById('appVersionsTotalPages');
    const prevButton = document.getElementById('appVersionsPrevPageBtn');
    const nextButton = document.getElementById('appVersionsNextPageBtn');
    currentPageElement.innerText = currentAppVersionPageNumber;
    totalPagesElement.innerText = totalAppVersionPagesCount;
    prevButton.disabled = currentAppVersionPageNumber === 1;
    nextButton.disabled = currentAppVersionPageNumber === totalAppVersionPagesCount;
    const attributes = {
        'data-appversions-total-pages': totalAppVersionPagesCount,
        'data-appversions-current-page': currentAppVersionPageNumber,
        'data-app-domain-id': appDomainId,
        'data-app-domain-name': appDomainName,
        'data-app-id': appId,
        'data-app-name': appName,
    };
    [prevButton, nextButton].forEach(button => {
        Object.entries(attributes).forEach(([key, value]) => {
            button.setAttribute(key, value);
        });
    });
}

function loadAppVersions(paginationButton) {
    const appDomainId = paginationButton.dataset.appDomainId;
    const appDomainName = paginationButton.dataset.appDomainName;
    const appId = paginationButton.dataset.appId;
    const appName = paginationButton.dataset.appName;

    const appVersionsListTotalPages = parseInt(paginationButton.dataset.appVersionsTotalPages, 10);
    const appVersionsListCurrentPage = parseInt(paginationButton.dataset.appVersionsCurrentPage, 10);
    const direction = parseInt(paginationButton.dataset.direction, 10);
    const newPageNumber = appVersionsListCurrentPage + direction;
    (newPageNumber >= 1 && newPageNumber <= appVersionsListTotalPages) ? processAppVersionListFetch(appId, appDomainId, appName, appDomainName, newPageNumber) : console.log("No more pages to fetch.");
}

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
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error("Failed to get IS artefact for the selected Application version:", error);
        });
}

function handleErrors(response) {
    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    return response.json();
}

function populateTableForApplications(tableSelector, data, rowGenerator, appDomainName, appDomainId) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = ''; // Clear table content
    data.forEach((item, index) => {
        const row = rowGenerator(item, index, appDomainName, appDomainId);
        tableBody.insertAdjacentHTML('beforeend', row);
    });
}

function updateAppsPagination(totalAppPagesCount, currentAppPageNumber, appDomainName, appDomainId) {
    const currentPageElement = document.getElementById('appsCurrentPage');
    const totalPagesElement = document.getElementById('appsTotalPages');
    const prevButton = document.getElementById('appsPrevPageBtn');
    const nextButton = document.getElementById('appsNextPageBtn');
    currentPageElement.innerText = currentAppPageNumber;
    totalPagesElement.innerText = totalAppPagesCount;
    prevButton.disabled = currentAppPageNumber === 1;
    nextButton.disabled = currentAppPageNumber === totalAppPagesCount;
    const attributes = {
        'data-apps-total-pages': totalAppPagesCount,
        'data-apps-current-page': currentAppPageNumber,
        'data-app-domain-id': appDomainId,
        'data-app-domain-name': appDomainName
    };
    [prevButton, nextButton].forEach(button => {
        Object.entries(attributes).forEach(([key, value]) => {
            button.setAttribute(key, value);
        });
    });
}

function populateTableForApplicationVersions(tableSelector, data, rowGenerator, appName, appId, appDomainId, appDomainName) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = '';
    data.forEach((item, index) => {
        const row = rowGenerator(item, index, appName, appId, appDomainId, appDomainName);
        tableBody.insertAdjacentHTML('beforeend', row);
    });
}

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

function generateVersionRows(version, index, appName, appId, appDomainId, appDomainName) {
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

function displayLoader(tableSelector) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = '<tr><td colspan="4" class="text-center">Loading...</td></tr>';
}

function showError(tableSelector, message) {
    const tableBody = document.querySelector(tableSelector);
    tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">${message}</td></tr>`;
}

function goBackToStep(stepCounter) {
    const accordions = document.querySelectorAll('.accordion-collapse');
    accordions.forEach(acc => acc.classList.remove('show'));
    const targetAccordion = document.getElementById(`collapse${stepCounter}`);
    if (targetAccordion) {
        targetAccordion.classList.add('show');  // Open the target accordion
    }

    document.querySelectorAll('.accordion-button').forEach(button => button.classList.remove('accordion-button-active'));
    document.querySelector(`#heading${stepCounter} .accordion-button`).classList.add('accordion-button-active');
    document.getElementById(`heading${stepCounter}`).scrollIntoView({behavior: 'smooth'});
}

function toggleDescription(button) {
    const shortDescription = button.previousElementSibling;
    const fullDescription = shortDescription.nextElementSibling;
    if (fullDescription.classList.contains('d-none')) {
        // Show full description
        fullDescription.classList.remove('d-none');
        shortDescription.classList.add('d-none');
        button.textContent = 'Hide';
    } else {
        fullDescription.classList.add('d-none');
        shortDescription.classList.remove('d-none');
        button.textContent = 'Show';
    }
}