# NotesGUI
<hr>

> Staff notes plugin with a GUI.

### Installation
1. Download the jar from [here](https://github.com/MatuMaKa/NotesGUI/releases/latest)
2. Put the jar in your `plugins/` folder
3. Restart your server (not reload)

### Update
1. Download the new jar from [here](https://github.com/MatuMaKa/NotesGUI/releases/latest)
2. Replace the old jar with the new one
3. Delete the old `config.yml` and `messages.yml` then restart your server

### Storage Backends
| Backend | Description |
|---------|-------------|
| `yml` | Default, no setup required |
| `mysql` | Recommended for larger servers |
| `redis` | For high-performance caching |

<hr>

<details>
    <summary><h3 style="display: inline;">Permissions</h3></summary>

| Permission                 | Description                                           |
|----------------------------|:------------------------------------------------------|
| `notesgui.command`         | Access to the `/notes` command                        |
| `notesgui.command.add`     | Permission to add notes to a player                   |
| `notesgui.command.view`    | Permission to view notes of a player                  |
| `notesgui.command.remove`  | Permission to remove a note                           |
| `notesgui.command.reload`  | Permission to use /notes reload                       |
| `notesgui.notify`          | Receive a notification when a player with notes joins |
</details>

<details>
    <summary><h3 style="display: inline;">Commands</h3></summary>

| Command | Description |
|---------|:------------|
| `/notes add <player> <note>` | Add a note to a player |
| `/notes view <player>` | Open the notes GUI for a player |
| `/notes remove <player> <id>` | Remove a note by ID |
| `/notes reload` | Reload the config and messages |
</details>

<details>
    <summary><h3 style="display: inline;">Configuration</h3></summary>

All settings are configurable in `config.yml`:
- Storage backend (yml, mysql, redis)
- GUI title, border, filler, and note item appearance
- MiniMessage and legacy `&` color code support
- Pagination for players with many notes

Messages are fully customizable in `messages.yml` with support for MiniMessage and `&` codes.
</details>

<details>
    <summary><h3 style="display: inline;">To-Do</h3></summary>

- [ ] Discord webhook integration
- [ ] Toggleable in-game notifications for note creation, deletion, and player join alerts (`/notes notify`)
- [ ] Remove legacy color code support 
</details>

### Compatibility
| | |
|--|--|
| **Server** | Paper 1.21.1+ |
| **Java** | 21+ |