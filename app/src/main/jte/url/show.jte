@import hexlet.code.dto.page.url.UrlPage
@param UrlPage page

@template.layout.app(
    page = page,
    content = @`
    !{var url = page.url();}
    <h1>Сайт: ${url.name()}</h1>
    <table>
        <tr>
            <td>ID</td>
            <td>${url.id()}</td>
        </tr>
        <tr>
            <td>Имя</td>
            <td>${url.name()}</td>
        </tr>
        <tr>
            <td>Дата создания</td>
            <td>${String.valueOf(url.insertedAt())}</td>
        </tr>
    </table>
    `
)
