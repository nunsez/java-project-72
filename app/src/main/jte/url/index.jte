@import hexlet.code.dto.page.url.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.app(
    page = page,
    content = @`
    !{var urls = page.urls();}
    <div class="container-lg mt-5">
        <h1>Сайты</h1>
        <table class="table table-bordered table-hover mt-3">
            <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
            </thead>
            <tbody>
                @for(var url : urls)
                    <tr>
                        <td>${url.id()}</td>
                        <td><a href="${NamedRoutes.urlPath(url)}">${url.name()}</a></td>

                        !{var lastCheck = page.lastCheckForUrl(url);}

                        @if(lastCheck == null)
                            <td></td>
                            <td></td>
                        @else
                            <td>${page.formatTimestamp(lastCheck.insertedAt())}</td>
                            <td>${lastCheck.statusCode()}</td>
                        @endif
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
    `
)
