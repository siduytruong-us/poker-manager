package com.duyts.pokerhost.fake

import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.Transaction
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.domain.repository.AuthUser
import com.duyts.pokerhost.domain.repository.SessionPerformance

object FakeData {
	val playerId = "user1"

	val player = Player(
		id = playerId,
		name = "Duy Truong",
		buyIn = 200f,
		cashOut = 450f
	)

	val session = PokerSession(
		id = "ses.1",
		title = "Friday Night Cash",
		players = listOf(player),
		completedAt = 1715000000000L
	)

	val performance = SessionPerformance(
		sessionId = "ses.1",
		sessionTitle = "High Stakes Saturday",
		completedAt = 1715000000000L,
		profit = 250f,
		buyIn = 500f,
		cashOut = 750f,
		adjustment = 0f
	)

	val user = AuthUser(
		id = "user123",
		email = "duy.truong@example.com",
		displayName = "Duy Truong",
		photoUrl = null
	)

	val performances = listOf(
		performance,
		SessionPerformance(
			sessionId = "2",
			sessionTitle = "Saturday Cash",
			completedAt = 1715100000000L,
			profit = -20f,
			buyIn = 100f,
			cashOut = 80f,
			adjustment = 0f
		),
		SessionPerformance(
			sessionId = "3",
			sessionTitle = "Home Game",
			completedAt = 1715200000000L,
			profit = 100f,
			buyIn = 200f,
			cashOut = 300f,
			adjustment = 0f
		)
	)

	val groupedSessions = mapOf(
		"Today" to listOf(
			PokerSession(
				id = "ses.1",
				title = "High Stakes Hold'em",
				ownerId = playerId,
				status = SessionStatus.ACTIVE,
				players = listOf(
					Player(id = playerId, name = "Player 1", buyIn = 1000f, cashOut = 0f),
					Player(id = "ply.2", name = "Player 2", buyIn = 3250f, cashOut = 0f)
				)
			)
		),
		"Yesterday" to listOf(
			PokerSession(
				id = "ses.2",
				title = "Friday Night Cash",
				ownerId = playerId,
				status = SessionStatus.COMPLETED,
				createdAt = 1715000000000L,
				completedAt = 1715010000000L,
				players = listOf(
					Player(id = playerId, name = "Player 1", buyIn = 200f, cashOut = 650f),
					Player(id = "ply.3", name = "Player 3", buyIn = 100f, cashOut = 50f)
				)
			),
			PokerSession(
				id = "ses.3",
				title = "Home Game Tournament",
				ownerId = playerId,
				status = SessionStatus.COMPLETED,
				createdAt = 1714000000000L,
				completedAt = 1714010000000L,
				players = listOf(
					Player(id = playerId, name = "Player 1", buyIn = 50f, cashOut = 0f)
				)
            )
        )
    )

    val detailSessionPlayers = listOf(
        Player(id = "ply.1", name = "Duy", buyIn = 5f, cashOut = 7f),
        Player(id = "ply.2", name = "John", buyIn = 5f, cashOut = 4f),
        Player(id = "ply.3", name = "Alice", buyIn = 10f, cashOut = 12f)
    )

    val detailSession = PokerSession(
        id = "ses.1",
        title = "Friday Night Poker",
        players = detailSessionPlayers,
        transactions = listOf(
            Transaction(
                id = "trx.1",
                type = TransactionType.BUY_IN,
                amount = 5f,
                playerId = "ply.1"
            ),
            Transaction(
                id = "trx.2",
                type = TransactionType.CASH_OUT,
                amount = 7f,
                playerId = "ply.1"
            ),
            Transaction(
                id = "trx.3",
                type = TransactionType.TRANSFER,
                amount = 1f,
                playerId = "ply.2",
                targetPlayerId = "ply.3"
			)
		)
	)
}
