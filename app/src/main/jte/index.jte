@import hexlet.code.util.NamedRoutes

@template.layout.app(
    content = @`
    <form action="${NamedRoutes.urlsPath()}" method="post">
        <label>
            Url
            <input type="text" name="url"/>
        </label>
        <label>
            <input type="submit" value="Добавить">
        </label>
    </form>
    `
)
