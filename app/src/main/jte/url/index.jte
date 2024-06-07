@import hexlet.code.dto.page.url.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.app(
    page = page,
    content = @`
    !{var urls = page.urls();}
    <table>
        <thead>
            <tr>
                <td>ID</td>
                <td>Имя</td>
                <td>Последняя проверка</td>
            </tr>
        </thead>
        <tbody>
            @for(var url : urls)
                <tr>
                    <td>${url.id()}</td>
                    <td><a href="${NamedRoutes.urlPath(url)}">${url.name()}</a></td>
                    <td>${String.valueOf(url.insertedAt())}</td>
                </tr>
            @endfor
        </tbody>
    </table>
    `
)
