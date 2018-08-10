package impl.commands;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import com.google.gson.Gson;
import core.command.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.InputStream;
import java.util.List;

@RegisterCommand
public class Anilist extends Command implements IInvocable<Anilist.Options> {
	public Anilist() {
		super(new Builder("Anilist")
				.withCommand("anilist")
		.withSchema("-u $"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		assertOption(!args.options.name.isEmpty(), "username parameter required");

		HttpClient          client = HttpClients.createDefault();
		HttpPost            post   = new HttpPost("https://graphql.anilist.co");
		List<NameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("query",
				"query ($id: Int) { \n" +
						"  User(id: $id, search: \"" + args.options.name + "\") {\n" +
						"    id\n" +
						"    name\n" +
						"    siteUrl\n" +
						"    moderatorStatus\n" +
						"    avatar {\n" +
						"      large\n" +
						"    }\n" +
						"    stats {\n" +
						"      watchedTime\n" +
						"      chaptersRead\n" +
						"      animeListScores {\n" +
						"        meanScore\n" +
						"      }\n" +
						"      mangaListScores {\n" +
						"        meanScore\n" +
						"      }\n" +
						"    }\n" +
						"  }\n" +
						"}"));
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse resp   = client.execute(post);
		HttpEntity   entity = resp.getEntity();
		if (entity != null) {
			try (InputStream in = entity.getContent()) {
				String      strResp = IOUtils.toString(in, "UTF-8");
				Gson        gson    = new Gson();
				AnilistInfo info    = gson.fromJson(strResp, AnilistInfo.class);
				RequestBuffer.request(() -> args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle(args.options.name + "'s AniList Info")
						.withThumbnail(info.data.User.avatar.large)
						.withUrl(info.data.User.siteUrl)
						.appendDesc("User ID: " + info.data.User.id + "\n")
						.appendDesc("Watched time: " + info.data.User.stats.watchedTime + " minutes\n")
//						.withDescription(info.data.User.stats.animeListScores.length + " items in anime list")
//						.withDescription(info.data.User.stats.mangaListScores.length + " items in manga list")
						.appendDesc("Chapters read: " + info.data.User.stats.chaptersRead)
						.build()
				));
			}
		}
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "username",
				abbrev = 'u',
				help = "The username to display info for",
				defaultValue = ""
		)
		public String name;
	}

	private class AnilistInfo {
		data data;
	}

	private class User {
		avatar avatar;
		int    id;
		String moderatorStatus;
		String name;
		String siteUrl;
		stats  stats;
	}

	private class avatar {
		String large;
	}

	private class data {
		User User;
	}

	private class stats {
		int[] animeListScores;
		int   chaptersRead;
		int[] mangaListScores;
		int   watchedTime;
	}
}
