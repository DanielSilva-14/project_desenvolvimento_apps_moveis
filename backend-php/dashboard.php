<?php
session_start();

require_once('db.php');

// check if admin is logged in
if (isset($_SESSION["dashboard_username"]) && $_SESSION["dashboard_username"] == "admin") {
    // user is safe
} else {
    header("Location: ./");
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>
    <header>
        <nav class="navbar">
            <div class="logo-container" onclick="changeFragment('default')">
                <img id="logo" src="assets/logo.png" alt="Logo">
                <span class="logo-title">SummitHub</span>
            </div>
            <ul class="nav-links">
                <li><a href="#" onclick="changeFragment('article_questions')">Questions</a></li>
                <li><a href="#" onclick="changeFragment('rooms')">Rooms</a></li>
                <li><a href="#" onclick="changeFragment('sessions')">Sessions</a></li>
                <li><a href="#" onclick="changeFragment('articles')">Articles</a></li>
                <li><a href="#" onclick="changeFragment('authors')">Authors</a></li>
                <li><a href="#" onclick="changeFragment('info')">Information</a></li>
                <li><a href="logout" class="logout">Admin <i class="bi bi-box-arrow-right"></i></a></li>
            </ul>
        </nav>
    </header>

    <div class="dashboard-content" id="default">
        Welcome to the SummitHub backoffice! <br>
        Click on any page on the sidebar to start managing content.
    </div>

    <div class="dashboard-content" id="article_questions" style="display: none;">
        <table>
            <td>Loading...</td>
        </table>
    </div>

    <div class="dashboard-content" id="rooms" style="display: none;">
        <table>
            <td>Loading...</td>
        </table>
    </div>

    <div class="dashboard-content" id="sessions" style="display: none;">
        <table>
            <td>Loading...</td>
        </table>
    </div>

    <div class="dashboard-content" id="articles" style="display: none;">
        <table>
            <td>Loading...</td>
        </table>
    </div>

    <div class="dashboard-content" id="authors" style="display: none;">
        <table>
            <td>Loading...</td>
        </table>
    </div>
    
    <div class="dashboard-content" id="info" style="display: none;">
        <form onsubmit="sendRequest(this, 'conferences')">
            <div class="input-container">
                <label for="name">Conference name</label>
                <input type="text" name="name" id="name">
            </div>
            
            <div class="input-container">
                <label for="information">Information</label>
                <textarea name="information" id="information"></textarea>
            </div>
            
            <input type="submit" class="update-info-button button" value="Update">
        </form>
    </div>

    <script>
        function changeFragment(fragmentID) {
            // Hide all fragments
            const allFragments = document.querySelectorAll(".dashboard-content");
            for (const fragment of allFragments) {
                fragment.style.display = "none";
            }

            // Show specified fragment
            const fragmentToShow = document.getElementById(fragmentID);
            fragmentToShow.style.display = "flex";
            if (fragmentID !== "info") getTable(fragmentID);
            else getInfo();

            // Reset state of all fragment links
            const allFragmentLinks = document.querySelectorAll(".nav-links a");
            for (const fragmentLink of allFragmentLinks) {
                fragmentLink.classList.remove("selected");
            }

            // Update state of shown fragment link
            const shownFragmentLink = document.querySelector(`#${fragmentID}_link`);
            shownFragmentLink.classList.add("selected");
        }

        async function sendRequest(form, table) {
            event.preventDefault();
            const response = await fetch(
                "backoffice_actions.php", {
                    method: 'post',
                    body: new FormData(form)
                });
            const actionFeedback = document.querySelector(".action-info");
            actionFeedback.innerHTML = await response.text();
            if (response.ok) {
                document.querySelector(".action-info").style.color = "#B0FF9D";
                setTimeout(
                    document.querySelector(".popup").remove(),
                    1000
                );
                getTable(table);
            } else {
                document.querySelector(".action-info").style.color = "orangered";
            }
        }

        async function getEntries(table) {
            const response = await fetch(
                "backoffice_actions.php", {
                    method: 'post',
                    body: new URLSearchParams({
                        "action": "getEntries",
                        "table": table
                    }),
                });
            return await response.json();
        }

        async function getTable(table) {
            const response = await fetch(
                "backoffice_actions.php", {
                    method: 'post',
                    body: new URLSearchParams({
                        "action": "getTable",
                        "table": table
                    }),
                });
            const tableElem = document.querySelector(`#${table} table`);
            tableElem.innerHTML = await response.text();
        }

        async function openInsertOrUpdatePopup(table, action, id = null) {
            // Create element
            const popup = document.createElement("div");
            popup.classList.add("popup");

            // Close on click outside box
            popup.addEventListener("click", e => e.target == popup ? popup.remove() : null);

            // Validade action
            if (!["insert", "update"].includes(action)) {
                alert("Invalid action");
                return;
            }

            // Insert common HTML
            popup.insertAdjacentHTML('beforeend', `
                <div class="create-container container">
                    <span class="container-label">${action} ${table.slice(0, -1)}</span>
                    <form method="post" onsubmit="sendRequest(this, '${table}')">
                        <input type="text" value="${action}" name="action" hidden>
                        <input type="text" value="${table}" name="table" hidden>
                        <input type="text" value="${id}" name="id" hidden>
                        <div class="action-info"></div>
                        <input type="submit" class="button" value="${action}">
                    </form>
                </div>
            `);

            // Get form element
            const form = popup.querySelector('form');

            if (table === "sessions") {
                // Insert field inputs
                form.insertAdjacentHTML('afterbegin', `
                    <input type="text" placeholder="Title" name="title">
                    <div class="input-container">
                        <label for="datetime_start">Choose start date and time</label>
                        <input type="datetime-local" name="datetime_start">
                    </div>
                    <div class="input-container">
                        <label for="datetime_end">Choose end date and time</label>
                        <input type="datetime-local" name="datetime_end">
                    </div>
                    <div class="input-container">
                        <label for="room_id">Choose room</label>
                        <select name="room_id"></select>
                    </div>
                `);

                // Populate dropdown
                const roomSelect = popup.querySelector("select[name=room_id]");
                const roomOptions = await getEntries("rooms");
                roomSelect.insertAdjacentHTML('beforeend', `<option value="0">No room selected</option>`);
                for (const room of roomOptions) {
                    roomSelect.insertAdjacentHTML('beforeend', `<option value="${room.id}">(${room.id}) ${room.name}</option>`);
                }

            } else if (table === "articles") {
                // Insert field inputs
                form.insertAdjacentHTML('afterbegin', `
                    <input type="text" placeholder="Title" name="title">
                    <div class="input-container">
                        <label for="author_id[]">Choose authors</label>
                        <select name="author_id[]" multiple></select>
                    </div>
                    <div class="input-container">
                        <label for="date_published">Choose published date</label>
                        <input type="date" name="date_published">
                    </div>
                    <div class="input-container">
                        <label for="session_id">Choose session</label>
                        <select name="session_id"></select>
                    </div>
                    <div class="input-container">
                        <label for="abstract">Abstract</label>
                        <textarea name="abstract" placeholder="Write or paste here the abstract"></textarea>
                    </div>
                    <div class="input-container">
                        <label for="pdf">PDF</label>
                        <input type="file" name="pdf" accept="application/pdf">
                    </div>
                `);

                // Populate dropdown (authors)
                const authorSelect = popup.querySelector("select[name='author_id[]']");
                const authorOptions = await getEntries("authors");
                authorSelect.insertAdjacentHTML('beforeend', `<option value="0">No author selected</option>`);
                for (const author of authorOptions) {
                    authorSelect.insertAdjacentHTML('beforeend', `<option value="${author.id}">(${author.id}) ${author.full_name}</option>`);
                }

                // Populate dropdown (sessions)
                const sessionSelect = popup.querySelector("select[name=session_id]");
                const sessionOptions = await getEntries("sessions");
                sessionSelect.insertAdjacentHTML('beforeend', `<option value="0">No session selected</option>`);
                for (const session of sessionOptions) {
                    sessionSelect.insertAdjacentHTML('beforeend', `<option value="${session.id}">(${session.id}) ${session.title}</option>`);
                }

            } else if (table === "rooms") {
                // Insert field inputs
                form.insertAdjacentHTML('afterbegin', `
                    <input type="text" placeholder="Name (eg. Room 4)" name="name">
                `);

            } else if (table === "authors") {
                // Insert field inputs
                form.insertAdjacentHTML('afterbegin', `
                    <input type="text" placeholder="Full name" name="fullname">
                `);
            }

            if (action === "update") {
                const entries = await getEntries(table);
                const row = entries.find(row => row.id === id);
                for (const [key, value] of Object.entries(row)) {
                    const inputElem = popup.querySelector(`[name=${key}]:not([type=file])`);
                    if (inputElem) inputElem.value = value;

                    // Handle relation tables
                    if (key === "authors" && value) {
                        const authors = value.split(", ");
                        for (const authorFullName of authors) {
                            const optionElem = Array.from(popup.querySelectorAll('select option')).find(option => option.innerHTML.includes(authorFullName));
                            optionElem.selected = true;
                        }
                    }
                }
            }

            // Add popup to document body
            document.body.appendChild(popup);
        }


        function openDeletePopup(table, ID) {
            const popup = document.createElement("div");
            popup.classList.add("popup");

            popup.innerHTML = `
            <div class="create-container container">
                <span class="container-label">Delete ${table}</span>
                <form method="post" onsubmit="sendRequest(this, '${table}')">
                    <div>Are you sure you want to delete ID ${ID} from ${table}?</div>
                    <input type="text" value="delete" name="action" hidden>
                    <input type="text" value="${table}" name="type" hidden>
                    <input type="text" value="${ID}" name="id" hidden>
                    <div class="action-info"></div>
                    <div class="insert-button button" onclick="document.querySelector('.popup').remove()">Cancel</div>
                    <input type="submit" class="delete-button button" value="Delete">
                </form>
            </div>`;

            document.body.appendChild(popup);
        }

        function openApprovePopup(table, ID, approve = true) {
            const popup = document.createElement("div");
            popup.classList.add("popup");

            popup.innerHTML = `
            <div class="create-container container">
                <span class="container-label">${approve ? 'Approve' : 'Disapprove'} ID ${ID} ${table}</span>
                <form method="post" onsubmit="sendRequest(this, '${table}')">
                    <div>Are you sure you want to ${approve ? 'approve' : 'disapprove'} ID ${ID} from ${table}?</div>
                    <input type="text" value="${approve ? 'approve' : 'disapprove'}" name="action" hidden>
                    <input type="text" value="${table}" name="type" hidden>
                    <input type="text" value="${ID}" name="id" hidden>
                    <div class="action-info"></div>
                    <input type="submit" class="insert-button button" value="${approve ? 'Approve' : 'Disapprove'}">
                    <div class="edit-button button" onclick="document.querySelector('.popup').remove()">Cancel</div>
                </form>
            </div>`;

            document.body.appendChild(popup);
        }

        async function getInfo() {
            const info = (await getEntries("conferences"))[0];
            for (const key in info) {
                if(key != "id") document.querySelector(`[name=${key}]`).value = info[key];
            }
        }

        function getNthParentNode(element, n) {
            var nodes = [];
            nodes.push(element);
            while (element.parentNode && nodes.length < n) {
                nodes.unshift(element.parentNode);
                element = element.parentNode;
            }
            return nodes[0];
        }
    </script>

    
</body>

</html>