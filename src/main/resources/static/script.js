document.addEventListener('DOMContentLoaded', function () {
    loadUsers();
    loadCurrentUser();
    actionsOnPage();
});

function actionsOnPage(){
    document.getElementById('profile-tab').addEventListener('click', () => addNewUser());
}

async function addNewUser(){
    const allRoles = await loadAllRoles();
    const roleSelect = document.getElementById('new_user_roleSelect');
    roleSelect.innerHTML = '';

    allRoles.forEach(role => {
        const option = document.createElement('option');
        // option.value = role.name;
        option.textContent = role.name;
        roleSelect.appendChild(option);
    });
}

async function loadUsers() {
    try {
        const response = await fetch('/admin/api/users');
        const users = await response.json();
        const userTableBody = document.getElementById('usersTableBody');

        users.forEach(user => {
            const row = document.createElement('tr');

            row.appendChild(createCell(user.id));
            row.appendChild(createCell(user.name));
            row.appendChild(createCell(user.lastName));
            row.appendChild(createCell(user.age));
            row.appendChild(createCell(user.email));
            row.appendChild(createCell(user.roles.map(r => r.name.slice(5)).join(', ')));

            const actionCell = document.createElement('td');
            const editButton = document.createElement('button');
            editButton.textContent = 'Edit';
            editButton.className = 'btn btn-primary';
            editButton.setAttribute('data-bs-toggle', 'modal');
            editButton.setAttribute('data-bs-target', '#modalEdit');
            editButton.setAttribute('data-user-id', user.id);
            editButton.addEventListener('click', () => loadUserDataForEdit(user.id));
            actionCell.appendChild(editButton);
            row.appendChild(actionCell);

            const actionCell2 = document.createElement('td');
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.className = 'btn btn-danger';
            deleteButton.setAttribute('data-bs-toggle', 'modal');
            deleteButton.setAttribute('data-bs-target', '#modalDelete');
            deleteButton.setAttribute('data-user-id', user.id);
            deleteButton.addEventListener('click', () => loadUserDataForDelete(user.id));
            actionCell2.appendChild(deleteButton);
            row.appendChild(actionCell2);

            userTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка при загрузке списка пользователей:', error);
    }
}

function createCell(text) {
    const cell = document.createElement('td');
    cell.textContent = text;
    return cell;
}

async function loadAllRoles() {
    try {
        const response = await fetch('/admin/api/roles');
        return await response.json();
    } catch (error) {
        console.error('Ошибка при загрузке всех ролей:', error);
        return [];
    }
}

async function loadCurrentUser() {
    try {
        const navBar = document.getElementById('navBar');
        const span = document.createElement('span');
        const currentUserTableBody = document.getElementById('currentUserTableBody');
        const row = document.createElement('tr');


        const response = await fetch('/user/current_user');
        const currentUserInfo = await response.json();
        const role = currentUserInfo.roles.map(r => r.name.slice(5)).join(', ');
        span.textContent = currentUserInfo.email + ' with roles: ' + role;
        navBar.appendChild(span);
        row.appendChild(createCell(currentUserInfo.id));
        row.appendChild(createCell(currentUserInfo.name));
        row.appendChild(createCell(currentUserInfo.lastName));
        row.appendChild(createCell(currentUserInfo.age));
        row.appendChild(createCell(currentUserInfo.email));
        row.appendChild(createCell(role));
        currentUserTableBody.appendChild(row);
    } catch (error) {
        console.error('Ошибка при загрузке данных текущего пользователя', error);
        return [];
    }

}

async function loadUserDataForEdit(userId) {
    try {
        const response = await fetch('/admin/api/users/' + userId);
        const data = await response.json();

        document.getElementById('editModalID').value = data.id;
        document.getElementById('editModalName').value = data.name;
        document.getElementById('editModalLastName').value = data.lastName;
        document.getElementById('editModalEmail').value = data.email;
        document.getElementById('editModalAge').value = data.age;

        const allRoles = await loadAllRoles();
        const roleSelect = document.getElementById('edit_roleSelect');
        roleSelect.innerHTML = '';

        allRoles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.name;
            option.textContent = role.name;
            if (data.roles.some(userRole => userRole.name === role.name)) {
                option.selected = true;
            }
            roleSelect.appendChild(option);
        });

        const editButton = document.getElementById('editButton');
        // Удаляем события и создаем новое
        editButton.replaceWith(editButton.cloneNode(true));
        document.getElementById('editButton').addEventListener('click', () => sendEditRequest(userId));
    } catch (error) {
        console.error('Ошибка при загрузке данных пользователя:', error);
    }
}

async function sendEditRequest(userId) {
    const roleSelect = document.getElementById('edit_roleSelect');
    const selectedRoles = Array.from(roleSelect.selectedOptions).map(option => option.value);

    const userData = {
        id: userId,
        name: document.getElementById('editModalName').value,
        lastName: document.getElementById('editModalLastName').value,
        email: document.getElementById('editModalEmail').value,
        age: document.getElementById('editModalAge').value,
        password: document.getElementById('editModalPassword').value,
        roles: selectedRoles
    };

    try {
        const response = await fetch('/admin/edit/' + userId, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            console.log('Запрос успешно выполнен');

            const table = document.getElementById('usersTableBody');
            table.innerHTML = '';

            const modal = bootstrap.Modal.getInstance(document.getElementById('modalEdit'));
            modal.hide();

            loadUsers();
        } else {
            console.error('Ошибка при выполнении запроса:', response.statusText);
        }
    } catch (error) {
        console.error('Ошибка при выполнении запроса:', error);
    }
}

async function loadUserDataForDelete(userId) {
    try {
        const response = await fetch('/admin/api/users/' + userId);
        const data = await response.json();

        document.getElementById('deleteModalID').value = data.id;
        document.getElementById('deleteModalName').value = data.name;
        document.getElementById('deleteModalLastName').value = data.lastName;
        document.getElementById('deleteModalEmail').value = data.email;
        document.getElementById('deleteModalAge').value = data.age;

        const roleSelect = document.getElementById('delete_roleSelect');
        roleSelect.innerHTML = '';
        data.roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.name;
            option.textContent = role.name;
            roleSelect.appendChild(option);
        });

        const deleteButton = document.getElementById('deleteButton');
        deleteButton.setAttribute('data-user-id', userId);
        deleteButton.addEventListener('click', () => sendDeleteRequest(userId));
    } catch (error) {
        console.error('Ошибка при загрузке данных пользователя:', error);
    }
}

async function sendDeleteRequest(userId) {
    try {
        const response = await fetch('/admin/delete/' + userId, {
            method: 'POST'
        });

        if (response.ok) {
            console.log('Запрос успешно выполнен');

            const table = document.getElementById('usersTableBody');
            table.innerHTML = '';

            const modal = bootstrap.Modal.getInstance(document.getElementById('modalDelete'));
            modal.hide();

            loadUsers();
        } else {
            console.error('Ошибка при выполнении запроса:', response.statusText);
        }
    } catch (error) {
        console.error('Ошибка при выполнении запроса:', error);
    }
}
